package com.brio.gym.ui

import android.app.Application
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brio.gym.brio.Entrenador
import com.brio.gym.brio.Frases
import com.brio.gym.brio.Situacion
import com.brio.gym.data.Ajustes
import com.brio.gym.data.Almacen
import com.brio.gym.data.Datos
import com.brio.gym.data.EjercicioEnEntreno
import com.brio.gym.data.Entreno
import com.brio.gym.data.Serie
import com.brio.gym.data.Tono
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Estado de ánimo de Brío para animarlo en pantalla. */
enum class Animo { NORMAL, CONTENTO, EUFORICO }

class BrioViewModel(app: Application) : AndroidViewModel(app) {

    private val almacen = Almacen(app)
    private val mutexGuardado = Mutex()

    private val _datos = MutableStateFlow(Datos())
    val datos: StateFlow<Datos> = _datos.asStateFlow()

    private val _frase = MutableStateFlow("")
    val frase: StateFlow<String> = _frase.asStateFlow()

    private val _animo = MutableStateFlow(Animo.NORMAL)
    val animo: StateFlow<Animo> = _animo.asStateFlow()

    /** Segundos de descanso restantes; 0 = sin temporizador. */
    private val _descanso = MutableStateFlow(0)
    val descanso: StateFlow<Int> = _descanso.asStateFlow()
    private var trabajoDescanso: Job? = null

    init {
        val cargados = almacen.cargar()
        _datos.value = cargados
        val sinEntrenar = Entrenador.diasSinEntrenar(cargados.entrenos)
        decir(if (sinEntrenar != null && sinEntrenar >= 5) Situacion.VUELTA else Situacion.SALUDO)
    }

    // ---------- Brío habla ----------

    fun decir(situacion: Situacion) {
        val a = _datos.value.ajustes
        _frase.value = Frases.de(situacion, a.tono, a.lesionado, evitar = _frase.value)
        _animo.value = when (situacion) {
            Situacion.RECORD -> Animo.EUFORICO
            Situacion.SERIE, Situacion.FIN, Situacion.INICIO -> Animo.CONTENTO
            else -> Animo.NORMAL
        }
    }

    /** Tocar a Brío: frase nueva del saludo. */
    fun tocarBrio() = decir(Situacion.SALUDO)

    // ---------- Persistencia ----------

    private fun actualizar(cambio: (Datos) -> Datos) {
        val nuevos = cambio(_datos.value)
        _datos.value = nuevos
        viewModelScope.launch(Dispatchers.IO) {
            mutexGuardado.withLock { almacen.guardar(_datos.value) }
        }
    }

    private fun actualizarActivo(cambio: (Entreno) -> Entreno) = actualizar { d ->
        d.activo?.let { d.copy(activo = cambio(it)) } ?: d
    }

    // ---------- Entreno ----------

    fun empezarEntreno() {
        if (_datos.value.activo != null) return
        val ahora = System.currentTimeMillis()
        actualizar { it.copy(activo = Entreno(id = ahora, inicio = ahora)) }
        decir(Situacion.INICIO)
    }

    fun terminarEntreno() {
        val activo = _datos.value.activo ?: return
        // Quitamos ejercicios vacíos y series sin hacer.
        val limpio = activo.copy(
            fin = System.currentTimeMillis(),
            ejercicios = activo.ejercicios
                .map { e -> e.copy(series = e.series.filter { it.hecha }) }
                .filter { it.series.isNotEmpty() },
        )
        cancelarDescanso()
        actualizar { d ->
            d.copy(activo = null, entrenos = if (limpio.ejercicios.isEmpty()) d.entrenos else d.entrenos + limpio)
        }
        decir(Situacion.FIN)
    }

    fun descartarEntreno() {
        cancelarDescanso()
        actualizar { it.copy(activo = null) }
        decir(Situacion.SALUDO)
    }

    fun anadirEjercicio(id: String) {
        val sug = Entrenador.sugerencia(id, _datos.value.entrenos)
        val inicial = Serie(peso = sug?.peso ?: 0.0, reps = sug?.reps ?: 10)
        actualizarActivo { e -> e.copy(ejercicios = e.ejercicios + EjercicioEnEntreno(id, listOf(inicial, inicial, inicial))) }
    }

    fun quitarEjercicio(indice: Int) = actualizarActivo { e ->
        e.copy(ejercicios = e.ejercicios.filterIndexed { i, _ -> i != indice })
    }

    fun anadirSerie(indiceEj: Int) = actualizarActivo { e ->
        e.copy(ejercicios = e.ejercicios.mapIndexed { i, ej ->
            if (i != indiceEj) ej else ej.copy(series = ej.series + (ej.series.lastOrNull()?.copy(hecha = false) ?: Serie(reps = 10)))
        })
    }

    fun quitarSerie(indiceEj: Int, indiceSerie: Int) = actualizarActivo { e ->
        e.copy(ejercicios = e.ejercicios.mapIndexed { i, ej ->
            if (i != indiceEj) ej else ej.copy(series = ej.series.filterIndexed { j, _ -> j != indiceSerie })
        })
    }

    fun editarSerie(indiceEj: Int, indiceSerie: Int, peso: Double? = null, reps: Int? = null) = actualizarActivo { e ->
        e.copy(ejercicios = e.ejercicios.mapIndexed { i, ej ->
            if (i != indiceEj) ej else ej.copy(series = ej.series.mapIndexed { j, s ->
                if (j != indiceSerie) s else s.copy(peso = peso ?: s.peso, reps = reps ?: s.reps)
            })
        })
    }

    fun alternarSerie(indiceEj: Int, indiceSerie: Int) {
        val activo = _datos.value.activo ?: return
        val ej = activo.ejercicios.getOrNull(indiceEj) ?: return
        val serie = ej.series.getOrNull(indiceSerie) ?: return
        val marcando = !serie.hecha

        // ¿Récord? Comparamos con entrenos anteriores y con las series ya hechas hoy.
        val mejorHoy = ej.series.filterIndexed { j, s -> j != indiceSerie && s.hecha }.maxOfOrNull { it.peso } ?: 0.0
        val record = marcando && serie.peso > mejorHoy && Entrenador.esRecord(ej.ejercicioId, serie.peso, _datos.value.entrenos)

        actualizarActivo { e ->
            e.copy(ejercicios = e.ejercicios.mapIndexed { i, x ->
                if (i != indiceEj) x else x.copy(series = x.series.mapIndexed { j, s -> if (j == indiceSerie) s.copy(hecha = marcando) else s })
            })
        }
        if (marcando) {
            decir(if (record) Situacion.RECORD else Situacion.SERIE)
            if (record) vibrar(longitud = 400)
            iniciarDescanso()
        }
    }

    // ---------- Descanso ----------

    fun iniciarDescanso(segundos: Int = _datos.value.ajustes.descansoSeg) {
        trabajoDescanso?.cancel()
        _descanso.value = segundos
        trabajoDescanso = viewModelScope.launch {
            while (_descanso.value > 0) {
                delay(1000)
                _descanso.value = _descanso.value - 1
            }
            vibrar(longitud = 600)
            decir(Situacion.FIN_DESCANSO)
        }
    }

    fun sumarDescanso(seg: Int) {
        if (_descanso.value <= 0) return
        _descanso.value = (_descanso.value + seg).coerceAtLeast(1)
    }

    fun cancelarDescanso() {
        trabajoDescanso?.cancel()
        _descanso.value = 0
    }

    @Suppress("DEPRECATION")
    private fun vibrar(longitud: Long) {
        val v = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (v.hasVibrator()) v.vibrate(VibrationEffect.createOneShot(longitud, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    // ---------- Historial ----------

    fun borrarEntreno(id: Long) = actualizar { d -> d.copy(entrenos = d.entrenos.filter { it.id != id }) }

    // ---------- Ajustes ----------

    fun cambiarTono(tono: Tono) {
        actualizar { d -> d.copy(ajustes = d.ajustes.copy(tono = tono, mayorDeEdad = d.ajustes.mayorDeEdad || tono == Tono.SIN_PIEDAD)) }
        decir(Situacion.SALUDO)
    }

    fun cambiarAjustes(cambio: (Ajustes) -> Ajustes) = actualizar { d -> d.copy(ajustes = cambio(d.ajustes)) }
}
