package com.example.rpg.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.rpg.data.inventory.EquipmentSlot

@Composable
fun BackpackOutlineIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = size.minDimension * 0.09f)
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.18f, size.height * 0.28f),
            size = Size(size.width * 0.64f, size.height * 0.6f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                size.width * 0.13f,
                size.width * 0.13f,
            ),
            style = stroke,
        )
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * 0.34f, size.height * 0.08f),
            size = Size(size.width * 0.32f, size.height * 0.38f),
            style = stroke,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.3f, size.height * 0.58f),
            end = Offset(size.width * 0.7f, size.height * 0.58f),
            strokeWidth = size.minDimension * 0.07f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun EquipmentItemIcon(
    slot: EquipmentSlot,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Canvas(modifier = modifier) {
        val width = size.minDimension * 0.09f
        val stroke = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (slot) {
            EquipmentSlot.HEAD -> {
                drawArc(
                    color = color,
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.18f, size.height * 0.16f),
                    size = Size(size.width * 0.64f, size.height * 0.58f),
                    style = stroke,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.2f, size.height * 0.61f),
                    Offset(size.width * 0.8f, size.height * 0.61f),
                    width,
                    StrokeCap.Round,
                )
            }
            EquipmentSlot.CHEST -> {
                val path = Path().apply {
                    moveTo(size.width * 0.25f, size.height * 0.18f)
                    lineTo(size.width * 0.42f, size.height * 0.28f)
                    lineTo(size.width * 0.58f, size.height * 0.28f)
                    lineTo(size.width * 0.75f, size.height * 0.18f)
                    lineTo(size.width * 0.86f, size.height * 0.78f)
                    lineTo(size.width * 0.14f, size.height * 0.78f)
                    close()
                }
                drawPath(path, color, style = stroke)
            }
            EquipmentSlot.HANDS -> {
                drawRoundRect(
                    color,
                    topLeft = Offset(size.width * 0.22f, size.height * 0.25f),
                    size = Size(size.width * 0.56f, size.height * 0.55f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.16f),
                    style = stroke,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.3f, size.height * 0.42f),
                    Offset(size.width * 0.7f, size.height * 0.42f),
                    width,
                    StrokeCap.Round,
                )
            }
            EquipmentSlot.LEGS -> {
                val path = Path().apply {
                    moveTo(size.width * 0.25f, size.height * 0.15f)
                    lineTo(size.width * 0.75f, size.height * 0.15f)
                    lineTo(size.width * 0.67f, size.height * 0.84f)
                    lineTo(size.width * 0.52f, size.height * 0.84f)
                    lineTo(size.width * 0.5f, size.height * 0.48f)
                    lineTo(size.width * 0.48f, size.height * 0.84f)
                    lineTo(size.width * 0.33f, size.height * 0.84f)
                    close()
                }
                drawPath(path, color, style = stroke)
            }
            EquipmentSlot.FEET -> {
                drawArc(
                    color,
                    startAngle = 5f,
                    sweepAngle = 170f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.12f, size.height * 0.28f),
                    size = Size(size.width * 0.76f, size.height * 0.48f),
                    style = stroke,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.2f, size.height * 0.72f),
                    Offset(size.width * 0.82f, size.height * 0.72f),
                    width,
                    StrokeCap.Round,
                )
            }
            EquipmentSlot.WEAPON -> {
                drawLine(
                    color,
                    Offset(size.width * 0.72f, size.height * 0.15f),
                    Offset(size.width * 0.28f, size.height * 0.72f),
                    width,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.23f, size.height * 0.62f),
                    Offset(size.width * 0.42f, size.height * 0.79f),
                    width,
                    StrokeCap.Round,
                )
                drawCircle(
                    color,
                    radius = width * 0.65f,
                    center = Offset(size.width * 0.22f, size.height * 0.82f),
                )
            }
            EquipmentSlot.ARTIFACT -> {
                val path = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.12f)
                    lineTo(size.width * 0.82f, size.height * 0.42f)
                    lineTo(size.width * 0.5f, size.height * 0.88f)
                    lineTo(size.width * 0.18f, size.height * 0.42f)
                    close()
                }
                drawPath(path, color, style = stroke)
                drawCircle(color, radius = size.minDimension * 0.09f, center = center)
            }
        }
    }
}

@Composable
fun PlayerOutline(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.035f
        val stroke = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        drawOval(
            color = color,
            topLeft = Offset(size.width * 0.37f, size.height * 0.04f),
            size = Size(size.width * 0.26f, size.height * 0.18f),
            style = stroke,
        )
        val body = Path().apply {
            moveTo(size.width * 0.38f, size.height * 0.23f)
            cubicTo(
                size.width * 0.28f,
                size.height * 0.3f,
                size.width * 0.28f,
                size.height * 0.48f,
                size.width * 0.34f,
                size.height * 0.59f,
            )
            lineTo(size.width * 0.25f, size.height * 0.94f)
            moveTo(size.width * 0.62f, size.height * 0.23f)
            cubicTo(
                size.width * 0.72f,
                size.height * 0.3f,
                size.width * 0.72f,
                size.height * 0.48f,
                size.width * 0.66f,
                size.height * 0.59f,
            )
            lineTo(size.width * 0.75f, size.height * 0.94f)
            moveTo(size.width * 0.34f, size.height * 0.3f)
            lineTo(size.width * 0.13f, size.height * 0.61f)
            moveTo(size.width * 0.66f, size.height * 0.3f)
            lineTo(size.width * 0.87f, size.height * 0.61f)
            moveTo(size.width * 0.38f, size.height * 0.23f)
            cubicTo(
                size.width * 0.43f,
                size.height * 0.31f,
                size.width * 0.57f,
                size.height * 0.31f,
                size.width * 0.62f,
                size.height * 0.23f,
            )
            moveTo(size.width * 0.34f, size.height * 0.59f)
            cubicTo(
                size.width * 0.42f,
                size.height * 0.64f,
                size.width * 0.58f,
                size.height * 0.64f,
                size.width * 0.66f,
                size.height * 0.59f,
            )
        }
        drawPath(body, color, style = stroke)
        drawArc(
            color = color.copy(alpha = 0.5f),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Rect(
                Offset(size.width * 0.31f, size.height * 0.25f),
                Size(size.width * 0.38f, size.height * 0.3f),
            ).topLeft,
            size = Size(size.width * 0.38f, size.height * 0.3f),
            style = Stroke(strokeWidth * 0.65f),
        )
    }
}
