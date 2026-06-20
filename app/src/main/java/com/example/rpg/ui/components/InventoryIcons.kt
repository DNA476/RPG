package com.example.rpg.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.rpg.data.inventory.EquipmentSlot
import com.example.rpg.data.inventory.InventoryItem
import com.example.rpg.data.inventory.ItemIconType

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
fun InventoryItemIcon(
    item: InventoryItem,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Canvas(modifier = modifier) {
        val lineWidth = size.minDimension * 0.075f
        val stroke = Stroke(
            width = lineWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        when (item.iconType) {
            ItemIconType.HEADBAND -> drawHeadband(color, stroke, lineWidth)
            ItemIconType.HOOD -> {
                val hood = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.1f)
                    cubicTo(
                        size.width * 0.2f,
                        size.height * 0.18f,
                        size.width * 0.18f,
                        size.height * 0.58f,
                        size.width * 0.27f,
                        size.height * 0.84f,
                    )
                    lineTo(size.width * 0.73f, size.height * 0.84f)
                    cubicTo(
                        size.width * 0.82f,
                        size.height * 0.58f,
                        size.width * 0.8f,
                        size.height * 0.18f,
                        size.width * 0.5f,
                        size.height * 0.1f,
                    )
                    close()
                }
                drawPath(hood, color, style = stroke)
                drawOval(
                    color = color,
                    topLeft = Offset(size.width * 0.34f, size.height * 0.32f),
                    size = Size(size.width * 0.32f, size.height * 0.38f),
                    style = stroke,
                )
            }
            ItemIconType.CIRCLET -> {
                drawArc(
                    color = color,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.16f, size.height * 0.25f),
                    size = Size(size.width * 0.68f, size.height * 0.55f),
                    style = stroke,
                )
                drawDiamond(
                    color = color,
                    center = Offset(size.width * 0.5f, size.height * 0.32f),
                    radius = size.minDimension * 0.13f,
                    stroke = stroke,
                )
            }
            ItemIconType.CROWN -> {
                val crown = Path().apply {
                    moveTo(size.width * 0.16f, size.height * 0.3f)
                    lineTo(size.width * 0.34f, size.height * 0.5f)
                    lineTo(size.width * 0.5f, size.height * 0.2f)
                    lineTo(size.width * 0.66f, size.height * 0.5f)
                    lineTo(size.width * 0.84f, size.height * 0.3f)
                    lineTo(size.width * 0.76f, size.height * 0.78f)
                    lineTo(size.width * 0.24f, size.height * 0.78f)
                    close()
                }
                drawPath(crown, color, style = stroke)
                drawLine(
                    color,
                    Offset(size.width * 0.26f, size.height * 0.64f),
                    Offset(size.width * 0.74f, size.height * 0.64f),
                    lineWidth,
                    StrokeCap.Round,
                )
            }
            ItemIconType.VEST -> {
                drawChestArmor(color, stroke)
                drawLine(
                    color,
                    Offset(size.width * 0.5f, size.height * 0.29f),
                    Offset(size.width * 0.5f, size.height * 0.76f),
                    lineWidth,
                    StrokeCap.Round,
                )
            }
            ItemIconType.CLOAK -> {
                val cloak = Path().apply {
                    moveTo(size.width * 0.38f, size.height * 0.18f)
                    quadraticTo(size.width * 0.5f, size.height * 0.28f, size.width * 0.62f, size.height * 0.18f)
                    lineTo(size.width * 0.82f, size.height * 0.86f)
                    lineTo(size.width * 0.18f, size.height * 0.86f)
                    close()
                }
                drawPath(cloak, color, style = stroke)
                drawCircle(
                    color = color,
                    radius = lineWidth,
                    center = Offset(size.width * 0.5f, size.height * 0.28f),
                )
            }
            ItemIconType.CUIRASS -> {
                drawChestArmor(color, stroke)
                drawDiamond(
                    color = color,
                    center = center,
                    radius = size.minDimension * 0.15f,
                    stroke = stroke,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.28f, size.height * 0.63f),
                    Offset(size.width * 0.72f, size.height * 0.63f),
                    lineWidth,
                    StrokeCap.Round,
                )
            }
            ItemIconType.WRAPS -> {
                drawHandBase(color, stroke)
                repeat(3) { index ->
                    val y = size.height * (0.42f + index * 0.12f)
                    drawLine(
                        color,
                        Offset(size.width * 0.26f, y),
                        Offset(size.width * 0.72f, y),
                        lineWidth * 0.7f,
                        StrokeCap.Round,
                    )
                }
            }
            ItemIconType.GRIP -> {
                drawHandBase(color, stroke)
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.14f,
                    center = Offset(size.width * 0.5f, size.height * 0.58f),
                    style = stroke,
                )
            }
            ItemIconType.GAUNTLET -> {
                drawHandBase(color, stroke)
                val bolt = Path().apply {
                    moveTo(size.width * 0.56f, size.height * 0.34f)
                    lineTo(size.width * 0.41f, size.height * 0.56f)
                    lineTo(size.width * 0.54f, size.height * 0.56f)
                    lineTo(size.width * 0.43f, size.height * 0.78f)
                }
                drawPath(bolt, color, style = stroke)
            }
            ItemIconType.LEGGINGS -> {
                drawLegArmor(color, stroke)
                drawLine(
                    color,
                    Offset(size.width * 0.24f, size.height * 0.34f),
                    Offset(size.width * 0.76f, size.height * 0.34f),
                    lineWidth,
                    StrokeCap.Round,
                )
            }
            ItemIconType.TROUSERS -> {
                drawLegArmor(color, stroke)
                drawLine(
                    color,
                    Offset(size.width * 0.5f, size.height * 0.17f),
                    Offset(size.width * 0.5f, size.height * 0.47f),
                    lineWidth * 0.65f,
                    StrokeCap.Round,
                )
            }
            ItemIconType.GREAVES -> {
                drawLegArmor(color, stroke)
                drawLine(
                    color,
                    Offset(size.width * 0.3f, size.height * 0.55f),
                    Offset(size.width * 0.42f, size.height * 0.77f),
                    lineWidth,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.7f, size.height * 0.55f),
                    Offset(size.width * 0.58f, size.height * 0.77f),
                    lineWidth,
                    StrokeCap.Round,
                )
            }
            ItemIconType.BOOTS -> drawBoot(color, stroke, lineWidth)
            ItemIconType.SANDALS -> {
                drawBoot(color, stroke, lineWidth)
                drawLine(
                    color,
                    Offset(size.width * 0.37f, size.height * 0.4f),
                    Offset(size.width * 0.62f, size.height * 0.69f),
                    lineWidth,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.57f, size.height * 0.39f),
                    Offset(size.width * 0.35f, size.height * 0.67f),
                    lineWidth,
                    StrokeCap.Round,
                )
            }
            ItemIconType.WINGED_BOOTS -> {
                drawBoot(color, stroke, lineWidth)
                val wing = Path().apply {
                    moveTo(size.width * 0.63f, size.height * 0.45f)
                    quadraticTo(size.width * 0.93f, size.height * 0.23f, size.width * 0.86f, size.height * 0.58f)
                    quadraticTo(size.width * 0.98f, size.height * 0.5f, size.width * 0.78f, size.height * 0.72f)
                }
                drawPath(wing, color, style = stroke)
            }
            ItemIconType.SWORD -> drawSword(color, stroke, lineWidth)
            ItemIconType.FLAMING_SWORD -> {
                drawSword(color, stroke, lineWidth)
                val flame = Path().apply {
                    moveTo(size.width * 0.69f, size.height * 0.39f)
                    cubicTo(
                        size.width * 0.94f,
                        size.height * 0.25f,
                        size.width * 0.78f,
                        size.height * 0.12f,
                        size.width * 0.84f,
                        size.height * 0.08f,
                    )
                }
                drawPath(flame, color, style = stroke)
            }
            ItemIconType.DAGGER -> {
                drawLine(
                    color,
                    Offset(size.width * 0.66f, size.height * 0.2f),
                    Offset(size.width * 0.37f, size.height * 0.61f),
                    lineWidth * 1.2f,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.27f, size.height * 0.55f),
                    Offset(size.width * 0.48f, size.height * 0.7f),
                    lineWidth,
                    StrokeCap.Round,
                )
                drawCircle(color, lineWidth, Offset(size.width * 0.27f, size.height * 0.76f))
            }
            ItemIconType.HAMMER -> {
                drawLine(
                    color,
                    Offset(size.width * 0.61f, size.height * 0.37f),
                    Offset(size.width * 0.28f, size.height * 0.82f),
                    lineWidth,
                    StrokeCap.Round,
                )
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.36f, size.height * 0.14f),
                    size = Size(size.width * 0.5f, size.height * 0.3f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(lineWidth),
                    style = stroke,
                )
            }
            ItemIconType.STAFF -> {
                drawLine(
                    color,
                    Offset(size.width * 0.66f, size.height * 0.16f),
                    Offset(size.width * 0.3f, size.height * 0.86f),
                    lineWidth,
                    StrokeCap.Round,
                )
                drawCircle(
                    brush = Brush.radialGradient(listOf(color.copy(alpha = 0.25f), Color.Transparent)),
                    radius = size.minDimension * 0.26f,
                    center = Offset(size.width * 0.69f, size.height * 0.2f),
                )
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.13f,
                    center = Offset(size.width * 0.69f, size.height * 0.2f),
                    style = stroke,
                )
            }
            ItemIconType.STONE -> {
                val stone = Path().apply {
                    moveTo(size.width * 0.5f, size.height * 0.12f)
                    lineTo(size.width * 0.8f, size.height * 0.34f)
                    lineTo(size.width * 0.72f, size.height * 0.78f)
                    lineTo(size.width * 0.38f, size.height * 0.88f)
                    lineTo(size.width * 0.16f, size.height * 0.55f)
                    lineTo(size.width * 0.25f, size.height * 0.25f)
                    close()
                }
                drawPath(stone, color, style = stroke)
                drawLine(
                    color,
                    Offset(size.width * 0.25f, size.height * 0.25f),
                    Offset(size.width * 0.72f, size.height * 0.78f),
                    lineWidth * 0.65f,
                    StrokeCap.Round,
                )
            }
            ItemIconType.CHARM -> {
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.27f,
                    center = Offset(size.width * 0.5f, size.height * 0.58f),
                    style = stroke,
                )
                drawArc(
                    color = color,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.39f, size.height * 0.1f),
                    size = Size(size.width * 0.22f, size.height * 0.32f),
                    style = stroke,
                )
                drawCircle(color, lineWidth, center)
            }
            ItemIconType.COMPASS -> {
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.34f,
                    center = center,
                    style = stroke,
                )
                val needle = Path().apply {
                    moveTo(size.width * 0.62f, size.height * 0.27f)
                    lineTo(size.width * 0.53f, size.height * 0.55f)
                    lineTo(size.width * 0.38f, size.height * 0.73f)
                    lineTo(size.width * 0.47f, size.height * 0.45f)
                    close()
                }
                drawPath(needle, color, style = stroke)
            }
            ItemIconType.CORE -> {
                drawDiamond(color, center, size.minDimension * 0.3f, stroke)
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.12f,
                    center = center,
                    style = stroke,
                )
                drawCircle(
                    brush = Brush.radialGradient(listOf(color.copy(alpha = 0.35f), Color.Transparent)),
                    radius = size.minDimension * 0.34f,
                    center = center,
                )
            }
            ItemIconType.HOURGLASS -> {
                drawLine(
                    color,
                    Offset(size.width * 0.27f, size.height * 0.16f),
                    Offset(size.width * 0.73f, size.height * 0.16f),
                    lineWidth,
                    StrokeCap.Round,
                )
                drawLine(
                    color,
                    Offset(size.width * 0.27f, size.height * 0.84f),
                    Offset(size.width * 0.73f, size.height * 0.84f),
                    lineWidth,
                    StrokeCap.Round,
                )
                val glass = Path().apply {
                    moveTo(size.width * 0.33f, size.height * 0.2f)
                    cubicTo(
                        size.width * 0.35f,
                        size.height * 0.4f,
                        size.width * 0.65f,
                        size.height * 0.4f,
                        size.width * 0.67f,
                        size.height * 0.2f,
                    )
                    moveTo(size.width * 0.33f, size.height * 0.8f)
                    cubicTo(
                        size.width * 0.35f,
                        size.height * 0.6f,
                        size.width * 0.65f,
                        size.height * 0.6f,
                        size.width * 0.67f,
                        size.height * 0.8f,
                    )
                }
                drawPath(glass, color, style = stroke)
            }
            ItemIconType.EYE -> {
                val eye = Path().apply {
                    moveTo(size.width * 0.12f, size.height * 0.5f)
                    quadraticTo(size.width * 0.5f, size.height * 0.14f, size.width * 0.88f, size.height * 0.5f)
                    quadraticTo(size.width * 0.5f, size.height * 0.86f, size.width * 0.12f, size.height * 0.5f)
                    close()
                }
                drawPath(eye, color, style = stroke)
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.14f,
                    center = center,
                    style = stroke,
                )
                drawCircle(color, lineWidth, center)
            }
            ItemIconType.FEATHER -> {
                val feather = Path().apply {
                    moveTo(size.width * 0.24f, size.height * 0.82f)
                    cubicTo(
                        size.width * 0.34f,
                        size.height * 0.42f,
                        size.width * 0.62f,
                        size.height * 0.1f,
                        size.width * 0.82f,
                        size.height * 0.16f,
                    )
                    cubicTo(
                        size.width * 0.88f,
                        size.height * 0.44f,
                        size.width * 0.54f,
                        size.height * 0.72f,
                        size.width * 0.24f,
                        size.height * 0.82f,
                    )
                }
                drawPath(feather, color, style = stroke)
                drawLine(
                    color,
                    Offset(size.width * 0.25f, size.height * 0.82f),
                    Offset(size.width * 0.72f, size.height * 0.25f),
                    lineWidth * 0.7f,
                    StrokeCap.Round,
                )
            }
        }
    }
}
private fun DrawScope.drawHeadband(color: Color, stroke: Stroke, lineWidth: Float) {
    drawArc(
        color = color,
        startAngle = 195f,
        sweepAngle = 150f,
        useCenter = false,
        topLeft = Offset(size.width * 0.17f, size.height * 0.17f),
        size = Size(size.width * 0.66f, size.height * 0.58f),
        style = stroke,
    )
    drawLine(
        color,
        Offset(size.width * 0.2f, size.height * 0.58f),
        Offset(size.width * 0.8f, size.height * 0.58f),
        lineWidth,
        StrokeCap.Round,
    )
    drawLine(
        color,
        Offset(size.width * 0.72f, size.height * 0.58f),
        Offset(size.width * 0.83f, size.height * 0.78f),
        lineWidth * 0.7f,
        StrokeCap.Round,
    )
}

private fun DrawScope.drawChestArmor(color: Color, stroke: Stroke) {
    val armor = Path().apply {
        moveTo(size.width * 0.24f, size.height * 0.18f)
        lineTo(size.width * 0.41f, size.height * 0.28f)
        lineTo(size.width * 0.59f, size.height * 0.28f)
        lineTo(size.width * 0.76f, size.height * 0.18f)
        lineTo(size.width * 0.86f, size.height * 0.82f)
        lineTo(size.width * 0.14f, size.height * 0.82f)
        close()
    }
    drawPath(armor, color, style = stroke)
}

private fun DrawScope.drawHandBase(color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(size.width * 0.22f, size.height * 0.19f),
        size = Size(size.width * 0.56f, size.height * 0.66f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.width * 0.14f),
        style = stroke,
    )
}

private fun DrawScope.drawLegArmor(color: Color, stroke: Stroke) {
    val legs = Path().apply {
        moveTo(size.width * 0.23f, size.height * 0.14f)
        lineTo(size.width * 0.77f, size.height * 0.14f)
        lineTo(size.width * 0.67f, size.height * 0.86f)
        lineTo(size.width * 0.52f, size.height * 0.86f)
        lineTo(size.width * 0.5f, size.height * 0.48f)
        lineTo(size.width * 0.48f, size.height * 0.86f)
        lineTo(size.width * 0.33f, size.height * 0.86f)
        close()
    }
    drawPath(legs, color, style = stroke)
}

private fun DrawScope.drawBoot(color: Color, stroke: Stroke, lineWidth: Float) {
    val boot = Path().apply {
        moveTo(size.width * 0.3f, size.height * 0.17f)
        lineTo(size.width * 0.63f, size.height * 0.17f)
        lineTo(size.width * 0.64f, size.height * 0.58f)
        quadraticTo(size.width * 0.76f, size.height * 0.66f, size.width * 0.86f, size.height * 0.74f)
        quadraticTo(size.width * 0.68f, size.height * 0.88f, size.width * 0.22f, size.height * 0.8f)
        close()
    }
    drawPath(boot, color, style = stroke)
    drawLine(
        color,
        Offset(size.width * 0.24f, size.height * 0.8f),
        Offset(size.width * 0.82f, size.height * 0.8f),
        lineWidth,
        StrokeCap.Round,
    )
}

private fun DrawScope.drawSword(color: Color, stroke: Stroke, lineWidth: Float) {
    val blade = Path().apply {
        moveTo(size.width * 0.78f, size.height * 0.12f)
        lineTo(size.width * 0.68f, size.height * 0.5f)
        lineTo(size.width * 0.39f, size.height * 0.72f)
        lineTo(size.width * 0.3f, size.height * 0.63f)
        lineTo(size.width * 0.52f, size.height * 0.34f)
        close()
    }
    drawPath(blade, color, style = stroke)
    drawLine(
        color,
        Offset(size.width * 0.25f, size.height * 0.59f),
        Offset(size.width * 0.44f, size.height * 0.78f),
        lineWidth,
        StrokeCap.Round,
    )
    drawCircle(color, lineWidth, Offset(size.width * 0.24f, size.height * 0.82f))
}

private fun DrawScope.drawDiamond(
    color: Color,
    center: Offset,
    radius: Float,
    stroke: Stroke,
) {
    val diamond = Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x + radius, center.y)
        lineTo(center.x, center.y + radius)
        lineTo(center.x - radius, center.y)
        close()
    }
    drawPath(diamond, color, style = stroke)
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
