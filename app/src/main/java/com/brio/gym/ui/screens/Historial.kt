package com.brio.gym.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brio.gym.brio.Entrenador
import com.brio.gym.brio.fmt
import com.brio.gym.data.Ejercicios
import com.brio.gym.data.Entreno
import com.brio.gym.ui.theme.Dorado
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatoFecha = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM, HH:mm", Locale.forLanguageTag("es-ES"))

fun fechaBonita(ms: Long): String =
    Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).format(formatoFecha).replaceFirstChar { it.uppercase() }

@Composable
fun PantallaHistorial(entrenos: List<Entreno>, alBorrar: (Long) -> Unit, modifier: Modifier = Modifier) {
    val lista = entrenos.filter { it.fin != null }.sortedByDescending { it.inicio }
    var aBorrar by remember { mutableStateOf<Entreno?>(null) }

    LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Historial", style = MaterialTheme.typography.headlineLarge) }
        if (lista.isEmpty()) {
            item { Text("Aún no hay entrenos. Brío está esperando.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(lista, key = { it.id }) { e ->
            var abierto by remember { mutableStateOf(false) }
            Card(Modifier.fillMaxWidth().animateContentSize().clickable { abierto = !abierto }) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(fechaBonita(e.inicio), style = MaterialTheme.typography.titleMedium)
                            val min = ((e.fin ?: e.inicio) - e.inicio) / 60000
                            Text(
                                "$min min · ${e.ejercicios.size} ejercicios · ${fmt(e.volumen)} kg movidos",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { aBorrar = e }) { Icon(Icons.Filled.Delete, contentDescription = "Borrar") }
                    }
                    if (abierto) {
                        Spacer(Modifier.height(8.dp))
                        e.ejercicios.forEach { ej ->
                            Text(Ejercicios.nombre(ej.ejercicioId), style = MaterialTheme.typography.bodyLarge, color = Dorado)
                            Text(
                                ej.series.joinToString("   ") { "${fmt(it.peso)}×${it.reps}" },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }

    aBorrar?.let { e ->
        AlertDialog(
            onDismissRequest = { aBorrar = null },
            title = { Text("¿Borrar este entreno?") },
            text = { Text(fechaBonita(e.inicio)) },
            confirmButton = { TextButton(onClick = { alBorrar(e.id); aBorrar = null }) { Text("Borrar", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { aBorrar = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
fun PantallaMarcas(entrenos: List<Entreno>, modifier: Modifier = Modifier) {
    val marcas = Entrenador.marcas(entrenos)
    LazyColumn(modifier, contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Récords", style = MaterialTheme.typography.headlineLarge) }
        if (marcas.isEmpty()) {
            item { Text("Termina tu primer entreno y aquí aparecerán tus mejores marcas.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(marcas, key = { it.ejercicioId }) { m ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(Ejercicios.nombre(m.ejercicioId), style = MaterialTheme.typography.titleMedium)
                        Text(
                            "1RM estimado: ${fmt(m.rmEstimado)} kg · ${fechaBonita(m.fecha).substringBefore(",")}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${fmt(m.pesoMax)} kg", style = MaterialTheme.typography.headlineSmall, color = Dorado)
                        Text("× ${m.repsConPesoMax}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
