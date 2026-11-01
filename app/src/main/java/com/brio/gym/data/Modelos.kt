package com.brio.gym.data

/** Cuánta caña da Brío. */
enum class Tono { AMABLE, NORMAL, SIN_PIEDAD }

data class Serie(
    val peso: Double = 0.0,
    val reps: Int = 0,
    val hecha: Boolean = false,
)

data class EjercicioEnEntreno(
    val ejercicioId: String,
    val series: List<Serie>,
)

data class Entreno(
    val id: Long,
    val inicio: Long,
    val fin: Long? = null,
    val ejercicios: List<EjercicioEnEntreno> = emptyList(),
) {
    val volumen: Double
        get() = ejercicios.sumOf { e -> e.series.filter { it.hecha }.sumOf { it.peso * it.reps } }
}

data class Ajustes(
    val tono: Tono = Tono.NORMAL,
    val mayorDeEdad: Boolean = false,
    val descansoSeg: Int = 90,
    val lesionado: Boolean = false,
)

data class Datos(
    val ajustes: Ajustes = Ajustes(),
    val entrenos: List<Entreno> = emptyList(),
    val activo: Entreno? = null,
)
