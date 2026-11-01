package com.brio.gym.brio

import com.brio.gym.data.Tono

enum class Situacion {
    SALUDO,        // abrir la app
    VUELTA,        // vuelves tras varios días sin entrenar
    INICIO,        // empiezas un entreno
    SERIE,         // completas una serie
    RECORD,        // superas tu mejor marca en un ejercicio
    FIN_DESCANSO,  // se acaba el temporizador de descanso
    FIN,           // terminas el entreno
    DESCANSA,      // llevas muchos días seguidos: toca parar
    LESION,        // tienes activado el modo lesión
}

/**
 * Regla de oro (en todos los modos): Brío se mete con el esfuerzo y las excusas,
 * NUNCA con el cuerpo, el peso, la cara o la comida del usuario.
 */
object Frases {

    private val frases: Map<Situacion, Map<Tono, List<String>>> = mapOf(
        Situacion.SALUDO to mapOf(
            Tono.AMABLE to listOf(
                "¡Hola! Qué bien verte por aquí.",
                "Hoy es un buen día para sumar un poquito más.",
                "Paso a paso se llega lejos. ¿Vamos?",
                "Me alegra que hayas venido. Tú marcas el ritmo.",
                "Cada entreno cuenta, aunque sea corto.",
            ),
            Tono.NORMAL to listOf(
                "¿Otra vez aquí? Normal, entrenar con el mejor engancha.",
                "Llegó la estrella del día. Bueno, la segunda, después de mí.",
                "Ponte cómodo, que hoy vengo inspirado. Como siempre.",
                "Yo ya he calentado. Mentira, yo nací calentado.",
                "Tú pones las ganas, yo pongo el carisma. Trato justo.",
                "Hoy hay que darlo todo. Yo lo doy siempre, pero tú hoy también.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "¿Has venido a entrenar o a mirar el móvil? Decide rápido.",
                "Mueve el culo, que las pesas no se levantan solas. Bueno, las mías sí.",
                "Hoy no quiero excusas. Quiero series.",
                "El sofá te echa de menos. Que se aguante.",
                "Venga, que no tengo todo el día. Bueno, sí lo tengo, pero tú no.",
            ),
        ),
        Situacion.VUELTA to mapOf(
            Tono.AMABLE to listOf(
                "¡Has vuelto! Lo importante es retomar, sin prisa.",
                "Bienvenido de nuevo. Empezamos suave y vamos subiendo.",
                "Todos paramos a veces. Lo que cuenta es volver.",
            ),
            Tono.NORMAL to listOf(
                "Te he echado de menos. Bueno, me he echado de menos a mí entrenándote.",
                "¡Mira quién ha vuelto! Pensaba que te habías ido con otro entrenador.",
                "Unos días fuera y el gimnasio ya no era lo mismo. Sobre todo para ti.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "¿Unos días sin venir? Las mancuernas preguntaban por ti. Les dije que estabas de vacaciones mentales.",
                "Mira quién se acuerda de que tiene gimnasio. Venga, a recuperar el tiempo perdido.",
                "Tus excusas han entrenado más que tú esta semana. Se acabó.",
            ),
        ),
        Situacion.INICIO to mapOf(
            Tono.AMABLE to listOf(
                "¡Empezamos! Calienta bien y disfruta.",
                "Respira, concéntrate y a por ello.",
                "Hoy solo compites contigo de ayer.",
            ),
            Tono.NORMAL to listOf(
                "Entreno iniciado. Yo supervisaré con mi mirada de campeón.",
                "Vamos allá. Intenta impresionarme, que es difícil.",
                "Que empiece el espectáculo. Tú haces las series, yo pongo el estilo.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "Cronómetro en marcha. Ni se te ocurra pasarte 20 minutos en el móvil.",
                "Hoy vienes a sudar, no a hacerte fotos.",
                "A trabajar. Las quejas, por escrito y a la papelera.",
            ),
        ),
        Situacion.SERIE to mapOf(
            Tono.AMABLE to listOf(
                "¡Bien hecho! Una menos.",
                "Muy buena serie. Respira y a por la siguiente.",
                "Así se hace, con calma y buena técnica.",
                "Genial. Vas sumando.",
            ),
            Tono.NORMAL to listOf(
                "Serie completada. Casi con mi elegancia. Casi.",
                "Eso es. Se nota que tienes buen entrenador.",
                "Bien. Yo la habría hecho sonriendo, pero bien.",
                "Una más al saco. Sigue así y te pongo un póster mío de premio.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "Vale, una. ¿Quieres un aplauso o sigues?",
                "Hecha. No te acostumbres a que te felicite.",
                "Bien. Ahora repítelo sin poner esa cara de drama.",
                "¿Esa era la buena? Pues ahora otra mejor.",
            ),
        ),
        Situacion.RECORD to mapOf(
            Tono.AMABLE to listOf(
                "¡Récord personal! Mira todo lo que has avanzado.",
                "¡Nueva marca! Esto es fruto de tu constancia.",
                "¡Lo has conseguido! Hoy eres más fuerte que nunca.",
            ),
            Tono.NORMAL to listOf(
                "¡RÉCORD! Increíble. Casi tan impresionante como yo. Casi.",
                "Nueva marca. Voy a tener que empezar a preocuparme por mi puesto.",
                "Eso ha sido digno de mí. Y yo no digo eso a cualquiera.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "Récord nuevo. Vale, no está mal. Ahora repítelo sin llorar.",
                "Hostia, ¿eso lo has levantado tú? Pues mañana más.",
                "Récord. Disfrútalo cinco segundos. Ya. A la siguiente.",
            ),
        ),
        Situacion.FIN_DESCANSO to mapOf(
            Tono.AMABLE to listOf(
                "Descanso terminado. Cuando estés listo, seguimos.",
                "¡Listo! A por la siguiente serie.",
            ),
            Tono.NORMAL to listOf(
                "Se acabó el descanso. Yo no lo necesitaba, pero tú sí.",
                "Arriba. La barra no se va a levantar sola.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "Fin del descanso. Deja el móvil, que te estoy viendo.",
                "¿Sigues sentado? Venga, que se enfría el músculo.",
            ),
        ),
        Situacion.FIN to mapOf(
            Tono.AMABLE to listOf(
                "¡Entreno completado! Estoy muy orgulloso de ti.",
                "¡Hecho! Hidrátate y descansa bien.",
                "Un día más cumpliendo. Eso es lo que marca la diferencia.",
            ),
            Tono.NORMAL to listOf(
                "Entreno terminado. Hoy has estado casi a mi altura.",
                "Buen trabajo. Ya puedes ir diciendo que entrenas con el mejor.",
                "Fin. Si te preguntan, el mérito es 50 % tuyo y 50 % de mi carisma.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "Terminado. No ha estado mal. Tampoco te creas Hércules.",
                "Hecho. Mañana quiero lo mismo, pero sin tanta queja.",
                "Vale, has cumplido. Hoy te libras de mis comentarios. Bueno, de casi todos.",
            ),
        ),
        Situacion.DESCANSA to mapOf(
            Tono.AMABLE to listOf(
                "Llevas muchos días seguidos. Descansar también es entrenar.",
                "Tu cuerpo crece mientras descansa. Hoy podrías tomártelo libre.",
            ),
            Tono.NORMAL to listOf(
                "Hasta yo descanso, y eso que soy perfecto. Tómate un día libre.",
                "Demasiados días seguidos. Descansa, que te quiero entero para el próximo.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "Descansa, cabezota. Los músculos crecen en el sofá, no aquí.",
                "Hoy te prohíbo venir. Sí, yo. Descansa.",
            ),
        ),
        Situacion.LESION to mapOf(
            Tono.AMABLE to listOf(
                "Primero recuperarte. Haz solo lo que no te duela.",
                "Sin prisa. Escucha a tu cuerpo y, si duele, para.",
                "Lo importante ahora es curarte bien. Yo te espero.",
            ),
            Tono.NORMAL to listOf(
                "Modo cuidado activado. Hoy nada de heroicidades.",
                "Recupérate bien. Un campeón sabe cuándo bajar el ritmo.",
            ),
            Tono.SIN_PIEDAD to listOf(
                "Hoy no te voy a apretar: primero, recuperarte.",
            ),
        ),
    )

    /**
     * Devuelve una frase. Si el usuario está lesionado, Brío siempre se suaviza.
     * [evitar] permite no repetir la frase que se acaba de mostrar.
     */
    fun de(situacion: Situacion, tono: Tono, lesionado: Boolean = false, evitar: String? = null): String {
        val tonoReal = if (lesionado) Tono.AMABLE else tono
        val sit = if (lesionado && situacion == Situacion.SALUDO) Situacion.LESION else situacion
        val lista = frases[sit]?.get(tonoReal).orEmpty().ifEmpty { frases[sit]?.get(Tono.NORMAL).orEmpty() }
        if (lista.isEmpty()) return "¡Vamos!"
        val opciones = if (lista.size > 1) lista.filter { it != evitar } else lista
        return opciones.random()
    }
}
