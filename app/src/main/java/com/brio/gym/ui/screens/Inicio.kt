package com.brio.gym.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.brio.gym.R
import com.brio.gym.ui.theme.Fondo
import com.brio.gym.ui.theme.Naranja
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animación de arranque: Brío entra trotando desde la izquierda,
 * da un saltito, "resopla" y aparece el nombre.
 */
@Composable
fun PantallaArranque(alTerminar: () -> Unit) {
    val x = remember { Animatable(-900f) }
    val y = remember { Animatable(0f) }
    val inclinacion = remember { Animatable(-12f) }
    val escala = remember { Animatable(1f) }
    val texto = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Trote: avanza mientras bota
        launch {
            repeat(4) {
                y.animateTo(-30f, tween(90))
                y.animateTo(0f, tween(90))
            }
        }
        x.animateTo(0f, tween(720, easing = FastOutSlowInEasing))
        inclinacion.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        // Resoplido: se infla un poco y vuelve
        escala.animateTo(1.12f, tween(120))
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium))
        texto.animateTo(1f, tween(350))
        delay(550)
        alTerminar()
    }

    Box(Modifier.fillMaxSize().background(Fondo), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(
                Modifier
                    .size(170.dp)
                    .graphicsLayer {
                        translationX = x.value
                        translationY = y.value
                        rotationZ = inclinacion.value
                        scaleX = escala.value
                        scaleY = escala.value
                    }
                    .clip(CircleShape)
                    .background(Naranja),
            ) {
                Image(painterResource(R.drawable.brio_cara), contentDescription = null, modifier = Modifier.size(170.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Brío",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.graphicsLayer {
                    alpha = texto.value
                    translationY = (1f - texto.value) * 24f
                },
            )
            Text(
                "Tu entrenador. El mejor, según él.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer { alpha = texto.value },
            )
        }
    }
}
