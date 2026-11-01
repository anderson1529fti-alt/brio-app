package com.brio.gym.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brio.gym.brio.Entrenador
import com.brio.gym.brio.fmt
import com.brio.gym.brio.leerNumero
import com.brio.gym.data.Datos
import com.brio.gym.data.EjercicioEnEntreno
import com.brio.gym.data.Ejercicios
import com.brio.gym.data.Serie
import com.brio.gym.ui.Animo
import com.brio.gym.ui.BrioViewModel
import com.brio.gym.ui.components.BrioHablando
import com.brio.gym.ui.theme.Verde
import kotlinx.coroutines.delay

@Composable
fun PantallaEntreno(
    datos: Datos,
    frase: String,
    animo: Animo,
    descanso: Int,
    vm: BrioViewModel,
    modifier: Modifier = Modifier,
) {
    val activo = datos.activo ?: return
    var elegirEjercicio by remember { mutableStateOf(false) }
    var verDiscos by remember { mutableStateOf<Double?>(null) }
    var confirmarFin by remember { mutableStateOf(false) }
    var confirmarDescartar by remember { mutableStateOf(false) }

    // Reloj del entreno
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            ahora = System.currentTimeMillis()
        }
    }
    val minutos = (ahora - activo.inicio) / 60000

    Column(modifier) {
        // Cabecera
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Entrenando", style = MaterialTheme.typography.headlineSmall)
                Text("$minutos min", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = { confirmarDescartar = true }) { Text("Descartar") }
            Button(onClick = { confirmarFin = true }) { Text("Terminar") }
        }

        BrioHablando(frase, animo, tamano = 64.dp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

        if (descanso > 0) {
            BarraDescanso(
                restante = descanso,
                total = datos.ajustes.descansoSeg,
                alSumar = { vm.sumarDescanso(it) },
                alSaltar = { vm.cancelarDescanso() },
            )
        }

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(activo.ejercicios, key = { i, e -> "${e.ejercicioId}_$i" }) { i, ej ->
                TarjetaEjercicio(
                    ej = ej,
                    sugerencia = Entrenador.sugerencia(ej.ejercicioId, datos.entrenos)?.texto,
                    alEditar = { j, peso, reps -> vm.editarSerie(i, j, peso, reps) },
                    alMarcar = { j -> vm.alternarSerie(i, j) },
                    alAnadirSerie = { vm.anadirSerie(i) },
                    alQuitarSerie = { j -> vm.quitarSerie(i, j) },
                    alQuitar = { vm.quitarEjercicio(i) },
                    alDiscos = if (Ejercicios.deId(ej.ejercicioId)?.conBarra == true) {
                        { verDiscos = ej.series.lastOrNull()?.peso ?: 20.0 }
                    } else null,
                )
            }
            item {
                OutlinedButton(onClick = { elegirEjercicio = true }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Añadir ejercicio")
                }
            }
            item {
                TextButton(onClick = { verDiscos = 60.0 }, modifier = Modifier.fillMaxWidth()) {
                    Text("Calculadora de discos")
                }
            }
        }
    }

    if (elegirEjercicio) {
        DialogoEjercicios(alElegir = { vm.anadirEjercicio(it); elegirEjercicio = false }, alCerrar = { elegirEjercicio = false })
    }
    verDiscos?.let { inicial -> DialogoDiscos(inicial) { verDiscos = null } }

    if (confirmarFin) {
        AlertDialog(
            onDismissRequest = { confirmarFin = false },
            title = { Text("¿Terminar entreno?") },
            text = { Text("Se guardarán solo las series que hayas marcado como hechas.") },
            confirmButton = { Button(onClick = { confirmarFin = false; vm.terminarEntreno() }) { Text("Terminar") } },
            dismissButton = { TextButton(onClick = { confirmarFin = false }) { Text("Seguir") } },
        )
    }
    if (confirmarDescartar) {
        AlertDialog(
            onDismissRequest = { confirmarDescartar = false },
            title = { Text("¿Descartar entreno?") },
            text = { Text("Se borrará todo lo de este entreno. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmarDescartar = false; vm.descartarEntreno() }) {
                    Text("Descartar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmarDescartar = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun BarraDescanso(restante: Int, total: Int, alSumar: (Int) -> Unit, alSaltar: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Descanso", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("%d:%02d".format(restante / 60, restante % 60), style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (restante.toFloat() / total.coerceAtLeast(1)).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                OutlinedButton(onClick = { alSumar(-15) }) { Text("−15 s") }
                OutlinedButton(onClick = { alSumar(15) }) { Text("+15 s") }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = alSaltar) { Text("Saltar") }
            }
        }
    }
}

@Composable
private fun TarjetaEjercicio(
    ej: EjercicioEnEntreno,
    sugerencia: String?,
    alEditar: (Int, Double?, Int?) -> Unit,
    alMarcar: (Int) -> Unit,
    alAnadirSerie: () -> Unit,
    alQuitarSerie: (Int) -> Unit,
    alQuitar: () -> Unit,
    alDiscos: (() -> Unit)?,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(Ejercicios.nombre(ej.ejercicioId), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                if (alDiscos != null) TextButton(onClick = alDiscos) { Text("Discos") }
                IconButton(onClick = alQuitar) { Icon(Icons.Filled.Delete, contentDescription = "Quitar ejercicio") }
            }
            if (sugerencia != null) {
                Text(sugerencia, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(6.dp))
            }
            Row(Modifier.padding(vertical = 4.dp)) {
                Text("Serie", Modifier.width(44.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Kg", Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                Text("Reps", Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(Modifier.width(96.dp))
            }
            ej.series.forEachIndexed { j, s ->
                FilaSerie(
                    numero = j + 1,
                    serie = s,
                    alCambiarPeso = { alEditar(j, it, null) },
                    alCambiarReps = { alEditar(j, null, it) },
                    alMarcar = { alMarcar(j) },
                    alQuitar = { alQuitarSerie(j) },
                )
            }
            TextButton(onClick = alAnadirSerie) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Serie")
            }
        }
    }
}

@Composable
private fun FilaSerie(
    numero: Int,
    serie: Serie,
    alCambiarPeso: (Double) -> Unit,
    alCambiarReps: (Int) -> Unit,
    alMarcar: () -> Unit,
    alQuitar: () -> Unit,
) {
    val fondo = if (serie.hecha) Verde.copy(alpha = 0.12f) else Color.Transparent
    Row(
        Modifier.fillMaxWidth().background(fondo, MaterialTheme.shapes.small).padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("$numero", Modifier.width(44.dp), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        CampoNumero(
            valorInicial = if (serie.peso == 0.0) "" else fmt(serie.peso),
            decimal = true,
            alCambiar = { t ->
                val n = leerNumero(t)
                if (n != null) alCambiarPeso(n) else if (t.isBlank()) alCambiarPeso(0.0)
            },
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
        )
        CampoNumero(
            valorInicial = if (serie.reps == 0) "" else serie.reps.toString(),
            decimal = false,
            alCambiar = { t ->
                val n = t.trim().toIntOrNull()
                if (n != null) alCambiarReps(n) else if (t.isBlank()) alCambiarReps(0)
            },
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
        )
        Box(
            Modifier
                .size(44.dp)
                .background(if (serie.hecha) Verde else MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .clickable(onClick = alMarcar),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Check, contentDescription = "Marcar serie", tint = if (serie.hecha) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = alQuitar, modifier = Modifier.size(44.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Quitar serie", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Campo de texto numérico que guarda su propio texto para no pelearse con el cursor. */
@Composable
private fun CampoNumero(valorInicial: String, decimal: Boolean, alCambiar: (String) -> Unit, modifier: Modifier = Modifier) {
    var texto by remember { mutableStateOf(valorInicial) }
    // Si el valor cambia desde fuera (p. ej. al borrar una serie), sincronizamos.
    LaunchedEffect(valorInicial) {
        if ((leerNumero(texto) ?: 0.0) != (leerNumero(valorInicial) ?: 0.0)) texto = valorInicial
    }
    OutlinedTextField(
        value = texto,
        onValueChange = { nuevo ->
            val permitido = if (decimal) nuevo.all { it.isDigit() || it == ',' || it == '.' } else nuevo.all { it.isDigit() }
            if (permitido && nuevo.length <= 6) {
                texto = nuevo
                alCambiar(nuevo)
            }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number),
        modifier = modifier.heightIn(min = 48.dp),
    )
}

@Composable
private fun DialogoEjercicios(alElegir: (String) -> Unit, alCerrar: () -> Unit) {
    var grupo by remember { mutableStateOf(Ejercicios.grupos.first()) }
    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Añadir ejercicio") },
        text = {
            Column {
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(Ejercicios.grupos.size) { i ->
                        val g = Ejercicios.grupos[i]
                        FilterChip(selected = g == grupo, onClick = { grupo = g }, label = { Text(g) })
                    }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.heightIn(max = 360.dp)) {
                    val lista = Ejercicios.todos.filter { it.grupo == grupo }
                    items(lista.size) { i ->
                        val e = lista[i]
                        Text(
                            e.nombre,
                            modifier = Modifier.fillMaxWidth().clickable { alElegir(e.id) }.padding(vertical = 14.dp),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = alCerrar) { Text("Cerrar") } },
    )
}

@Composable
private fun DialogoDiscos(inicial: Double, alCerrar: () -> Unit) {
    var objetivo by remember { mutableStateOf(fmt(inicial)) }
    var barra by remember { mutableStateOf(20.0) }
    val peso = leerNumero(objetivo) ?: 0.0
    val (discos, sobra) = Entrenador.discos(peso, barra)

    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Calculadora de discos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    label = { Text("Peso total (kg)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(20.0, 15.0, 10.0).forEach { b ->
                        FilterChip(selected = barra == b, onClick = { barra = b }, label = { Text("Barra ${fmt(b)}") })
                    }
                }
                when {
                    peso <= barra -> Text("Con la barra sola ya llegas (${fmt(barra)} kg).")
                    else -> {
                        Text("Por cada lado:", fontWeight = FontWeight.Bold)
                        Text(discos.joinToString("  +  ") { fmt(it) }, style = MaterialTheme.typography.headlineSmall)
                        if (sobra > 0.01) {
                            Text(
                                "No cuadra exacto con discos normales: faltan ${fmt(sobra)} kg en total.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = alCerrar) { Text("Listo") } },
    )
}
