package com.brio.gym.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Guarda todo en un único archivo JSON dentro del móvil.
 * Para el alfa es suficiente; más adelante se puede pasar a una base de datos (Room).
 */
class Almacen(context: Context) {

    private val archivo = File(context.filesDir, "brio.json")

    fun cargar(): Datos {
        if (!archivo.exists()) return Datos()
        return try {
            val raiz = JSONObject(archivo.readText())
            Datos(
                ajustes = ajustesDe(raiz.optJSONObject("ajustes")),
                entrenos = raiz.optJSONArray("entrenos")?.let { arr ->
                    (0 until arr.length()).map { entrenoDe(arr.getJSONObject(it)) }
                } ?: emptyList(),
                activo = raiz.optJSONObject("activo")?.let { entrenoDe(it) },
            )
        } catch (e: Exception) {
            // Si el archivo está dañado, se guarda una copia y se empieza de cero.
            archivo.copyTo(File(archivo.parentFile, "brio_danado.json"), overwrite = true)
            Datos()
        }
    }

    fun guardar(datos: Datos) {
        val raiz = JSONObject()
        raiz.put("version", 1)
        raiz.put("ajustes", JSONObject().apply {
            put("tono", datos.ajustes.tono.name)
            put("mayorDeEdad", datos.ajustes.mayorDeEdad)
            put("descansoSeg", datos.ajustes.descansoSeg)
            put("lesionado", datos.ajustes.lesionado)
        })
        raiz.put("entrenos", JSONArray().apply { datos.entrenos.forEach { put(entrenoAJson(it)) } })
        datos.activo?.let { raiz.put("activo", entrenoAJson(it)) }

        // Escritura segura: primero a un temporal y luego se reemplaza.
        val tmp = File(archivo.parentFile, "brio.json.tmp")
        tmp.writeText(raiz.toString())
        tmp.renameTo(archivo)
    }

    private fun ajustesDe(o: JSONObject?): Ajustes {
        if (o == null) return Ajustes()
        return Ajustes(
            tono = runCatching { Tono.valueOf(o.optString("tono", "NORMAL")) }.getOrDefault(Tono.NORMAL),
            mayorDeEdad = o.optBoolean("mayorDeEdad", false),
            descansoSeg = o.optInt("descansoSeg", 90),
            lesionado = o.optBoolean("lesionado", false),
        )
    }

    private fun entrenoDe(o: JSONObject): Entreno {
        val ejArr = o.optJSONArray("ejercicios") ?: JSONArray()
        return Entreno(
            id = o.getLong("id"),
            inicio = o.getLong("inicio"),
            fin = if (o.has("fin") && !o.isNull("fin")) o.getLong("fin") else null,
            ejercicios = (0 until ejArr.length()).map { i ->
                val e = ejArr.getJSONObject(i)
                val sArr = e.optJSONArray("series") ?: JSONArray()
                EjercicioEnEntreno(
                    ejercicioId = e.getString("id"),
                    series = (0 until sArr.length()).map { j ->
                        val s = sArr.getJSONObject(j)
                        Serie(s.optDouble("peso", 0.0), s.optInt("reps", 0), s.optBoolean("hecha", false))
                    },
                )
            },
        )
    }

    private fun entrenoAJson(e: Entreno): JSONObject = JSONObject().apply {
        put("id", e.id)
        put("inicio", e.inicio)
        e.fin?.let { put("fin", it) }
        put("ejercicios", JSONArray().apply {
            e.ejercicios.forEach { ej ->
                put(JSONObject().apply {
                    put("id", ej.ejercicioId)
                    put("series", JSONArray().apply {
                        ej.series.forEach { s ->
                            put(JSONObject().apply {
                                put("peso", s.peso)
                                put("reps", s.reps)
                                put("hecha", s.hecha)
                            })
                        }
                    })
                })
            }
        })
    }
}
