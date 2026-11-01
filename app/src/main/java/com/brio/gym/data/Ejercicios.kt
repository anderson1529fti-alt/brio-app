package com.brio.gym.data

data class Ejercicio(
    val id: String,
    val nombre: String,
    val grupo: String,
    /** Si se hace con barra olímpica (para la calculadora de discos y el salto de peso). */
    val conBarra: Boolean = false,
)

object Ejercicios {
    val todos: List<Ejercicio> = listOf(
        Ejercicio("press_banca", "Press de banca", "Pecho", conBarra = true),
        Ejercicio("press_inclinado", "Press inclinado con mancuernas", "Pecho"),
        Ejercicio("aperturas", "Aperturas en polea", "Pecho"),
        Ejercicio("fondos", "Fondos en paralelas", "Pecho"),

        Ejercicio("dominadas", "Dominadas", "Espalda"),
        Ejercicio("remo_barra", "Remo con barra", "Espalda", conBarra = true),
        Ejercicio("jalon", "Jalón al pecho", "Espalda"),
        Ejercicio("remo_mancuerna", "Remo con mancuerna", "Espalda"),
        Ejercicio("peso_muerto", "Peso muerto", "Espalda", conBarra = true),

        Ejercicio("sentadilla", "Sentadilla", "Piernas", conBarra = true),
        Ejercicio("prensa", "Prensa de piernas", "Piernas"),
        Ejercicio("zancadas", "Zancadas", "Piernas"),
        Ejercicio("peso_muerto_rumano", "Peso muerto rumano", "Piernas", conBarra = true),
        Ejercicio("curl_femoral", "Curl femoral", "Piernas"),
        Ejercicio("extension_cuadriceps", "Extensión de cuádriceps", "Piernas"),
        Ejercicio("gemelos", "Elevación de gemelos", "Piernas"),
        Ejercicio("hip_thrust", "Hip thrust", "Piernas", conBarra = true),

        Ejercicio("press_militar", "Press militar", "Hombros", conBarra = true),
        Ejercicio("elevaciones_laterales", "Elevaciones laterales", "Hombros"),
        Ejercicio("face_pull", "Face pull", "Hombros"),

        Ejercicio("curl_biceps", "Curl de bíceps", "Brazos"),
        Ejercicio("curl_martillo", "Curl martillo", "Brazos"),
        Ejercicio("triceps_polea", "Extensión de tríceps en polea", "Brazos"),
        Ejercicio("press_frances", "Press francés", "Brazos"),

        Ejercicio("plancha", "Plancha (reps = segundos)", "Core"),
        Ejercicio("crunch_polea", "Crunch en polea", "Core"),
        Ejercicio("elevacion_piernas", "Elevación de piernas colgado", "Core"),
    )

    private val porId = todos.associateBy { it.id }

    fun nombre(id: String): String = porId[id]?.nombre ?: id
    fun deId(id: String): Ejercicio? = porId[id]
    val grupos: List<String> = todos.map { it.grupo }.distinct()
}
