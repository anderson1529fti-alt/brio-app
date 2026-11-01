package com.brio.gym.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brio.gym.data.Ajustes
import com.brio.gym.data.Tono

@Composable
fun PantallaAjustes(
    ajustes: Ajustes,
    alCambiarTono: (Tono) -> Unit,
    alCambiar: ((Ajustes) -> Ajustes) -> Unit,
    alProbar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pedirEdad by remember { mutableStateOf(false) }
    var descanso by remember(ajustes.descansoSeg) { mutableFloatStateOf(ajustes.descansoSeg.toFloat()) }

    Column(
        modifier.verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineLarge)

        Text("Personalidad de Brío", style = MaterialTheme.typography.titleMedium)
        OpcionTono("Amable", "Siempre positivo, sin bromas a tu costa. Apto para todos.", ajustes.tono == Tono.AMABLE) {
            alCambiarTono(Tono.AMABLE)
        }
        OpcionTono("Normal", "Fanfarrón y egocéntrico, pero siempre de tu lado.", ajustes.tono == Tono.NORMAL) {
            alCambiarTono(Tono.NORMAL)
        }
        OpcionTono("Sin piedad (+18)", "Duro, sarcástico y con alguna palabrota. Se mete con tus excusas, nunca contigo.", ajustes.tono == Tono.SIN_PIEDAD) {
            if (ajustes.mayorDeEdad) alCambiarTono(Tono.SIN_PIEDAD) else pedirEdad = true
        }
        OutlinedButton(onClick = alProbar) { Text("Probar a Brío") }

        Spacer(Modifier.height(4.dp))
        Text("Descanso entre series: ${descanso.toInt()} s", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = descanso,
            onValueChange = { descanso = it },
            onValueChangeFinished = { alCambiar { a -> a.copy(descansoSeg = descanso.toInt()) } },
            valueRange = 30f..240f,
            steps = 13,
        )

        Card {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Modo lesión", fontWeight = FontWeight.Bold)
                    Text(
                        "Brío se vuelve amable y te recuerda ir con cuidado. Si algo duele, para.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Switch(checked = ajustes.lesionado, onCheckedChange = { v -> alCambiar { it.copy(lesionado = v) } })
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Brío · versión 0.1 alfa", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            "Brío da consejos generales de entrenamiento y no sustituye a un profesional sanitario ni a un entrenador titulado.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    if (pedirEdad) {
        AlertDialog(
            onDismissRequest = { pedirEdad = false },
            title = { Text("Modo sin piedad") },
            text = { Text("Este modo usa lenguaje duro y alguna palabrota. ¿Confirmas que eres mayor de 18 años?") },
            confirmButton = { Button(onClick = { pedirEdad = false; alCambiarTono(Tono.SIN_PIEDAD) }) { Text("Soy mayor de 18") } },
            dismissButton = { TextButton(onClick = { pedirEdad = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun OpcionTono(titulo: String, descripcion: String, elegido: Boolean, alElegir: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = alElegir)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = elegido, onClick = alElegir)
            Column(Modifier.padding(start = 6.dp)) {
                Text(titulo, fontWeight = FontWeight.Bold)
                Text(descripcion, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
