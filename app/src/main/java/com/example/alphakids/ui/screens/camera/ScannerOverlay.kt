package com.example.alphakids.ui.screens.camera

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphaKidsTextGreen
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {
    // --- Lógica de Animación ---
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_transition")
    val scannerYPosition by infiniteTransition.animateFloat(
        initialValue = 0.25f, // Comienza al 25% de la altura
        targetValue = 0.75f, // Termina al 75% de la altura
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scanner_y_position"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val cornerLength = 80f
        val strokeWidth = 15f
        val rectSize = Size(canvasWidth * 0.7f, canvasHeight * 0.5f)
        val rectTopLeft = Offset(
            (canvasWidth - rectSize.width) / 2,
            (canvasHeight - rectSize.height) / 2
        )

        // Dibujamos un fondo oscuro semitransparente para resaltar el marco
        drawRect(color = Color.Black.copy(alpha = 0.5f))

        // "Cortamos" un rectángulo transparente en el centro
        drawRoundRect(
            topLeft = rectTopLeft,
            size = rectSize,
            color = Color.Transparent,
            blendMode = BlendMode.Clear,
            cornerRadius = CornerRadius(28.dp.toPx())
        )

        // --- Dibujamos las Esquinas ---
        // Arriba-Izquierda
        drawPath(
            color = AlphaKidsTextGreen,
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(rectTopLeft.x, rectTopLeft.y + cornerLength)
                lineTo(rectTopLeft.x, rectTopLeft.y)
                lineTo(rectTopLeft.x + cornerLength, rectTopLeft.y)
            },
            style = Stroke(width = strokeWidth)
        )
        // Arriba-Derecha
        drawPath(
            color = AlphaKidsTextGreen,
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(rectTopLeft.x + rectSize.width - cornerLength, rectTopLeft.y)
                lineTo(rectTopLeft.x + rectSize.width, rectTopLeft.y)
                lineTo(rectTopLeft.x + rectSize.width, rectTopLeft.y + cornerLength)
            },
            style = Stroke(width = strokeWidth)
        )
        // Abajo-Izquierda
        drawPath(
            color = AlphaKidsTextGreen,
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(rectTopLeft.x, rectTopLeft.y + rectSize.height - cornerLength)
                lineTo(rectTopLeft.x, rectTopLeft.y + rectSize.height)
                lineTo(rectTopLeft.x + cornerLength, rectTopLeft.y + rectSize.height)
            },
            style = Stroke(width = strokeWidth)
        )
        // Abajo-Derecha
        drawPath(
            color = AlphaKidsTextGreen,
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(rectTopLeft.x + rectSize.width - cornerLength, rectTopLeft.y + rectSize.height)
                lineTo(rectTopLeft.x + rectSize.width, rectTopLeft.y + rectSize.height)
                lineTo(rectTopLeft.x + rectSize.width, rectTopLeft.y + rectSize.height - cornerLength)
            },
            style = Stroke(width = strokeWidth)
        )

        // --- Dibujamos la Línea Animada de Escaneo ---
        val lineY = lerp(
            start = Offset(0f, rectTopLeft.y),
            stop = Offset(0f, rectTopLeft.y + rectSize.height),
            fraction = scannerYPosition
        ).y

        drawLine(
            color = AlphaKidsTextGreen,
            start = Offset(rectTopLeft.x, lineY),
            end = Offset(rectTopLeft.x + rectSize.width, lineY),
            strokeWidth = 5f
        )
    }
}

fun lerp(start: Offset, stop: Offset, fraction: Float): Offset {
    return start + (stop - start) * fraction
}


@Preview
@Composable
fun ScannerOverlayPreview() {
    AlphakidsTheme {
        ScannerOverlay()
    }
}