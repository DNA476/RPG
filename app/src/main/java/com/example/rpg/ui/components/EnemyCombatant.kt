package com.example.rpg.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.rpg.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val HIT_ANIMATION_DURATION_MS = 420

/**
 * Renders the current enemy and plays a complete hit reaction for every hit event.
 */
@Composable
fun EnemyCombatant(
    imageResource: String,
    enemyName: String,
    hitEventId: Long,
    damageMessage: String?,
    modifier: Modifier = Modifier,
) {
    val shake = remember { Animatable(0f) }
    val redFlash = remember { Animatable(0f) }
    val slashAlpha = remember { Animatable(0f) }
    val slashScale = remember { Animatable(0.65f) }
    val shakeDistancePx = with(LocalDensity.current) { 18.dp.toPx() }

    LaunchedEffect(hitEventId) {
        if (hitEventId == 0L) return@LaunchedEffect

        coroutineScope {
            launch {
                shake.snapTo(0f)
                shake.animateTo(
                    targetValue = 0f,
                    animationSpec = keyframes {
                        durationMillis = HIT_ANIMATION_DURATION_MS
                        -shakeDistancePx at 45
                        shakeDistancePx at 95
                        -shakeDistancePx * 0.7f at 150
                        shakeDistancePx * 0.55f at 215
                        -shakeDistancePx * 0.3f at 285
                    },
                )
            }
            launch {
                redFlash.snapTo(0f)
                redFlash.animateTo(1f, tween(durationMillis = 70))
                redFlash.animateTo(0f, tween(durationMillis = 280))
            }
            launch {
                slashAlpha.snapTo(0f)
                slashScale.snapTo(0.65f)
                slashAlpha.animateTo(1f, tween(durationMillis = 55))
                slashAlpha.animateTo(0f, tween(durationMillis = 260))
            }
            launch {
                slashScale.animateTo(1.2f, tween(durationMillis = 315))
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(enemyDrawableResource(imageResource)),
            contentDescription = enemyName,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(
                color = Color(
                    red = 1f,
                    green = 1f - redFlash.value * 0.72f,
                    blue = 1f - redFlash.value * 0.72f,
                ),
                blendMode = BlendMode.Modulate,
            ),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = shake.value
                    rotationZ = shake.value / shakeDistancePx * 1.8f
                },
        )

        Image(
            painter = painterResource(R.drawable.sword_slash),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(270.dp)
                .graphicsLayer {
                    alpha = slashAlpha.value
                    scaleX = slashScale.value
                    scaleY = slashScale.value
                    rotationZ = -32f
                },
        )

        DamagePopup(
            message = damageMessage,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-12).dp, y = 42.dp),
        )
    }
}
