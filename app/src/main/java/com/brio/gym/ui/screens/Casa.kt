package com.brio.gym.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brio.gym.brio.Entrenador
import com.brio.gym.brio.Situacion
import com.brio.gym.brio.fmt
import com.brio.gym.data.Datos
import com.brio.gym.ui.Animo
import com.brio.gym.ui.components.BrioHablando
import com.brio.gym.ui.theme.Dorado

@Composable
fun PantallaCasa(
    datos: Datos,
    frase: String,
    animo: Animo,
    alTocarBrio: () -> Unit,
    alEmpezar: () -> Unit,
    alPedirConsejo: (Situacion) -> Unit,
    modifier: Modifier = Modifier,
) {
    val racha = Entrenador.rachaSemanas(datos.entrenos)
    val seguidos = Entrenador.diasSeguidos(datos.entrenos)
    val terminados = datos.entrenos.filter { it.fin != null }
    val volumenSemana = terminados
        .filter { System.currentTimeMillis() - it.inicio < 7L * 24 * 3600 * 1000 }
        .sumOf { it.volumen }

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Hoy", style = MaterialTheme.typography.headlineLarge)

        BrioHablando(frase = frase, animo = animo, tamano = 104.dp, alTocar = alTocarBrio)
        Text(
            "Toca a Brío para que diga otra cosa.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Dato("🔥 Racha", if (racha == 1) "1 semana" else "$racha semanas", Modifier.weight(1f))
            Dato("💪 Entrenos", "${terminados.size}", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Dato("📦 Volumen 7 días", "${fmt(volumenSemana)} kg", Modifier.weight(1f))
            Dato("📅 Días seguidos", "$seguidos", Modifier.weight(1f))
        }

        if (seguidos >= 6) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Llevas $seguidos días seguidos", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Brío recomienda un día de descanso. Los músculos crecen mientras descansas.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { alPedirConsejo(Situacion.DESCANSA) }) { Text("¿Qué opina Brío?") }
                }
            }
        }

        if (datos.ajustes.lesionado) {
            Card {
                Text(
                    "Modo lesión activado: Brío va con cuidado. Si algo duele, para y consulta a un profesional.",
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Button(
            onClick = alEmpezar,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            colors = ButtonDefaults.buttonColors(),
        ) {
            Text("EMPEZAR ENTRENO", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun Dato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(titulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(valor, style = MaterialTheme.typography.headlineSmall, color = Dorado)
        }
    }
}
