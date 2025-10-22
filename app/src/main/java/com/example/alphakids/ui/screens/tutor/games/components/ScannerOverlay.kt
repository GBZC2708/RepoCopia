import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun ScannerOverlay(modifier: Modifier = Modifier) {

    val primaryColor = MaterialTheme.colorScheme.primary
    val lineThickness = 5.dp

    // Configuración de la animación de la línea
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_line")
    val linePosition by infiniteTransition.animateFloat(
        initialValue = 0.1f, // Empezar cerca de la parte superior
        targetValue = 0.9f,  // Terminar cerca de la parte inferior
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "line_position"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val (width, height) = size

        // Definir el área del marco
        val framePadding = 48.dp.toPx()
        val left = framePadding
        val top = framePadding
        val right = width - framePadding
        val bottom = height - framePadding

        // 1. Dibujar el MARCO (Esquinas y línea superior)
        val cornerRadius = 28.dp.toPx()
        val cornerLength = 60.dp.toPx()
        val strokeWidth = 8.dp.toPx()

        val framePath = Path().apply {
            // Top-Left
            moveTo(left, top + cornerLength)
            lineTo(left, top + cornerRadius)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(left, top, left + (cornerRadius * 2), top + (cornerRadius * 2)),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(left + cornerLength, top)

            // Top-Right
            moveTo(right - cornerLength, top)
            lineTo(right - cornerRadius, top)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(right - (cornerRadius * 2), top, right, top + (cornerRadius * 2)),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(right, top + cornerLength)

            // Bottom-Left
            moveTo(left, bottom - cornerLength)
            lineTo(left, bottom - cornerRadius)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(left, bottom - (cornerRadius * 2), left + (cornerRadius * 2), bottom),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(left + cornerLength, bottom)

            // Bottom-Right
            moveTo(right - cornerLength, bottom)
            lineTo(right - cornerRadius, bottom)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(right - (cornerRadius * 2), bottom - (cornerRadius * 2), right, bottom),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            lineTo(right, bottom - cornerLength)

            // Línea superior (según tu imagen)
            moveTo(left + cornerLength + cornerRadius, top)
            lineTo(right - cornerLength - cornerRadius, top)
        }

        drawPath(
            path = framePath,
            color = primaryColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 2. Dibujar la LÍNEA DE ESCANEO (Animada)
        val lineY = top + (bottom - top) * linePosition

        drawLine(
            color = primaryColor,
            start = Offset(left, lineY),
            end = Offset(right, lineY),
            strokeWidth = lineThickness.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
fun ScannerOverlayPreview() {
    AlphakidsTheme {
        ScannerOverlay(modifier = Modifier.padding(16.dp))
    }
}
