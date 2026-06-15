package com.example.rpg.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun SwordInShieldOutlineIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Canvas(modifier = modifier) {
        val stroke = 2.4.dp.toPx()
        val shield = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.18f)
            lineTo(size.width * 0.5f, size.height * 0.08f)
            lineTo(size.width * 0.82f, size.height * 0.18f)
            lineTo(size.width * 0.76f, size.height * 0.62f)
            quadraticTo(
                size.width * 0.68f,
                size.height * 0.84f,
                size.width * 0.5f,
                size.height * 0.94f,
            )
            quadraticTo(
                size.width * 0.32f,
                size.height * 0.84f,
                size.width * 0.24f,
                size.height * 0.62f,
            )
            close()
        }
        drawPath(
            path = shield,
            color = color,
            style = Stroke(width = stroke, join = StrokeJoin.Round),
        )

        drawLine(
            color = color,
            start = Offset(size.width * 0.68f, size.height * 0.2f),
            end = Offset(size.width * 0.34f, size.height * 0.68f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        val swordTip = Path().apply {
            moveTo(size.width * 0.68f, size.height * 0.2f)
            lineTo(size.width * 0.72f, size.height * 0.08f)
            lineTo(size.width * 0.62f, size.height * 0.15f)
            close()
        }
        drawPath(path = swordTip, color = color)
        drawLine(
            color = color,
            start = Offset(size.width * 0.27f, size.height * 0.62f),
            end = Offset(size.width * 0.42f, size.height * 0.73f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.31f, size.height * 0.73f),
            end = Offset(size.width * 0.25f, size.height * 0.8f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}
