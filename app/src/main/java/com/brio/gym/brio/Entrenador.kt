package com.brio.gym.brio

import com.brio.gym.data.Ejercicios
import com.brio.gym.data.Entreno
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.roundToInt

data class Sugerencia(val peso: Double, val reps: Int, val texto: String)

data class Marca(val ejercicioId: String, val pesoMax: Double, val repsConPesoMax: Int, val rmEstimado: Double, val fecha: Long)

/** Lógica de Brío con reglas fijas: instantánea y sin IA. */
object Entrenador {

    private val zona: ZoneId get() = ZoneId.systemDefault()

    private fun dia(ms: Long): LocalDate = Instant.ofEpochMilli(ms).atZone(zona).toLocalDate()

    private fun terminados(entrenos: List<Entreno>) = entrenos.filter { it.fin != null }

    /** Semanas seguidas con al menos un entreno (la semana actual cuenta aunque aún no hayas entrenado). */
    fun rachaSemanas(entrenos: List<Entreno>): Int {
        val campo = WeekFields.of(Locale.forLanguageTag("es-ES"))
        fun claveSemana(d: LocalDate) = d.get(campo.weekBasedYear()) * 100 + d.get(campo.weekOfWeekBasedYear())
        val semanas = terminados(entrenos).map { claveSemana(dia(it.inicio)) }.toSet()
        var d = LocalDate.now(zona)
        if (claveSemana(d) !in semanas) d = d.minusWeeks(1)
        var racha = 0
        while (claveSemana(d) in semanas) {
            racha++
            d = d.minusWeeks(1)
        }
        return racha
    }

    /** Días seguidos entrenando, contando hasta hoy o ayer. */
    fun diasSeguidos(entrenos: List<Entreno>): Int {
        val dias = terminados(entrenos).map { dia(it.inicio) }.toSet()
        var d = LocalDate.now(zona)
        if (d !in dias) d = d.minusDays(1)
        var n = 0
        while (d in dias) {
            n++
            d = d.minusDays(1)
        }
        return n
    }

    /** Días desde el último entreno terminado, o null si nunca has entrenado. */
    fun diasSinEntrenar(entrenos: List<Entreno>): Long? {
        val ultimo = terminados(entrenos).maxOfOrNull { it.inicio } ?: return null
        return ChronoUnit.DAYS.between(dia(ultimo), LocalDate.now(zona))
    }

    /**
     * Progresión con reglas: si en la última sesión completaste todas las series,
     * sube peso; si no, repite el mismo.
     */
    fun sugerencia(ejercicioId: String, entrenos: List<Entreno>): Sugerencia? {
        val ultima = terminados(entrenos)
            .sortedByDescending { it.inicio }
            .firstNotNullOfOrNull { e -> e.ejercicios.firstOrNull { it.ejercicioId == ejercicioId && it.series.isNotEmpty() } }
            ?: return null
        val hechas = ultima.series.filter { it.hecha }
        if (hechas.isEmpty()) return null
        val top = hechas.maxBy { it.peso }
        val todasHechas = ultima.series.all { it.hecha }
        val salto = if (Ejercicios.deId(ejercicioId)?.conBarra == true) 2.5 else 1.0
        return if (todasHechas && top.peso > 0) {
            Sugerencia(top.peso + salto, top.reps, "Brío sugiere: ${fmt(top.peso + salto)} kg × ${top.reps} (la última vez lo completaste todo)")
        } else {
            Sugerencia(top.peso, top.reps, "Brío sugiere: repetir ${fmt(top.peso)} kg × ${top.reps} hasta completar todas las series")
        }
    }

    /** Mejor peso en cada ejercicio entre los entrenos terminados. */
    fun marcas(entrenos: List<Entreno>): List<Marca> {
        val mapa = mutableMapOf<String, Marca>()
        for (e in terminados(entrenos)) {
            for (ej in e.ejercicios) {
                for (s in ej.series.filter { it.hecha && it.peso > 0 }) {
                    val rm = rm(s.peso, s.reps)
                    val actual = mapa[ej.ejercicioId]
                    if (actual == null) {
                        mapa[ej.ejercicioId] = Marca(ej.ejercicioId, s.peso, s.reps, rm, e.inicio)
                    } else {
                        val mejorPeso = s.peso > actual.pesoMax || (s.peso == actual.pesoMax && s.reps > actual.repsConPesoMax)
                        mapa[ej.ejercicioId] = actual.copy(
                            pesoMax = if (mejorPeso) s.peso else actual.pesoMax,
                            repsConPesoMax = if (mejorPeso) s.reps else actual.repsConPesoMax,
                            fecha = if (mejorPeso) e.inicio else actual.fecha,
                            rmEstimado = maxOf(actual.rmEstimado, rm),
                        )
                    }
                }
            }
        }
        return mapa.values.sortedBy { Ejercicios.nombre(it.ejercicioId) }
    }

    /** ¿Esta serie supera el mejor peso que tenías antes de este entreno? */
    fun esRecord(ejercicioId: String, peso: Double, entrenos: List<Entreno>): Boolean {
        if (peso <= 0) return false
        val previo = marcas(entrenos).firstOrNull { it.ejercicioId == ejercicioId }?.pesoMax ?: return false
        return peso > previo
    }

    /** 1RM estimado (fórmula de Epley). */
    fun rm(peso: Double, reps: Int): Double = if (reps <= 1) peso else peso * (1 + reps / 30.0)

    /** Discos por lado para llegar a [objetivo] con una barra de [barra] kg. */
    fun discos(objetivo: Double, barra: Double): Pair<List<Double>, Double> {
        val disponibles = listOf(25.0, 20.0, 15.0, 10.0, 5.0, 2.5, 1.25)
        var porLado = (objetivo - barra) / 2
        val usados = mutableListOf<Double>()
        if (porLado <= 0) return emptyList<Double>() to 0.0
        for (d in disponibles) {
            while (porLado + 1e-9 >= d) {
                usados += d
                porLado -= d
            }
        }
        return usados to (porLado * 2)
    }
}

/** Formato español: 62,5 / 60 */
fun fmt(n: Double): String {
    val r = (n * 100).roundToInt() / 100.0
    return if (r % 1.0 == 0.0) r.toLong().toString() else r.toString().replace('.', ',')
}

fun leerNumero(s: String): Double? = s.trim().replace(',', '.').toDoubleOrNull()
