package com.brio.gym.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.brio.gym.R
import com.brio.gym.ui.Animo
import com.brio.gym.ui.theme.Naranja

/**
 * Brío respirando (sube y baja) y dando un salto cuando cambia su ánimo o su frase.
 * El arte es provisional para el alfa.
 */
@Composable
fun BrioAvatar(
    tamano: Dp,
    animo: Animo,
    disparador: Any?,
    modifier: Modifier = Modifier,
    alTocar: (() -> Unit)? = null,
) {
    val infinita = rememberInfiniteTransition(label = "respirar")
    val respiro by infinita.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "respiro",
    )

    val salto = remember { Animatable(0f) }
    val giro = remember { Animatable(0f) }
    LaunchedEffect(disparador, animo) {
        val altura = when (animo) {
            Animo.EUFORICO -> -40f
            Animo.CONTENTO -> -18f
            Animo.NORMAL -> -8f
        }
        salto.animateTo(altura, tween(140))
        salto.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
        if (animo == Animo.EUFORICO) {
            giro.animateTo(360f, tween(500))
            giro.snapTo(0f)
        }
    }

    val clic = if (alTocar != null) {
        Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { alTocar() }
    } else Modifier

    Box(
        modifier = modifier
            .size(tamano)
            .graphicsLayer {
                translationY = salto.value + respiro * -4f
                rotationY = giro.value
                val escala = 1f + respiro * 0.015f
                scaleX = escala
                scaleY = escala
            }
            .clip(CircleShape)
            .background(Naranja)
            .then(clic),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.brio_cara),
            contentDescription = "Brío",
            modifier = Modifier.size(tamano),
        )
    }
}

/** Brío con su bocadillo de texto. */
@Composable
fun BrioHablando(
    frase: String,
    animo: Animo,
    modifier: Modifier = Modifier,
    tamano: Dp = 88.dp,
    alTocar: (() -> Unit)? = null,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        BrioAvatar(tamano = tamano, animo = animo, disparador = frase, alTocar = alTocar)
        Spacer(Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            AnimatedContent(
                targetState = frase,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) },
                label = "frase",
            ) { texto ->
                Text(
                    text = texto,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                )
            }
        }
    }
}
