import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

/**
 * Overlay tipo escáner con "ventana" central, esquinas y línea animada.
 * Reporta el rectángulo de escaneo vía [onBoxRectChange] en coordenadas normalizadas (0..1).
 */
@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    boxWidthPercent: Float = 0.75f, // ancho de la ventana como % del ancho disponible
    boxAspectRatio: Float = 1f,     // alto = ancho / aspectRatio (1 = cuadrado)
    cornerLength: Dp = 28.dp,
    cornerStroke: Dp = 6.dp,
    cornerRadius: Dp = 16.dp,
    scrimColor: Color = Color.Black.copy(alpha = 0.55f),
    laserColor: Color = MaterialTheme.colorScheme.primary,
    // Si se proporciona, la animación de la línea se sincroniza con este tick (normalmente, los segundos restantes del temporizador)
    driveWithTick: Int? = null,
    driveDurationMillis: Int = 1000,
    onBoxRectChange: ((left: Float, top: Float, right: Float, bottom: Float) -> Unit)? = null
) {
    val lineThickness = 3.dp

    // Animación de la línea
    val lineProgress: Float = if (driveWithTick == null) {
        val infiniteTransition = rememberInfiniteTransition(label = "scanner_line")
        val p by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2400),
                repeatMode = RepeatMode.Reverse
            ),
            label = "line_position"
        )
        p
    } else {
        val target = if ((driveWithTick % 2) == 0) 0f else 1f
        val p by animateFloatAsState(
            targetValue = target,
            animationSpec = tween(driveDurationMillis),
            label = "line_position_tick"
        )
        p
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        // Calcular ventana
        val boxW = (w * boxWidthPercent).coerceIn(0f, w)
        val boxH = (boxW / boxAspectRatio).coerceAtMost(h * 0.9f)
        val actualBoxW = boxW
        val actualBoxH = boxH
        val left = (w - actualBoxW) / 2f
        val top = (h - actualBoxH) / 2f
        val right = left + actualBoxW
        val bottom = top + actualBoxH

        // Notificar rect normalizado
        onBoxRectChange?.invoke(left / w, top / h, right / w, bottom / h)

        // Scrim con agujero
        val scrimPath = Path().apply {
            fillType = PathFillType.EvenOdd
            addRect(Rect(0f, 0f, w, h))
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = left,
                    top = top,
                    right = right,
                    bottom = bottom,
                    radiusX = cornerRadius.toPx(),
                    radiusY = cornerRadius.toPx()
                )
            )
        }
        drawPath(path = scrimPath, color = scrimColor)

        // Esquinas
        val cl = cornerLength.toPx()
        val cs = cornerStroke.toPx()
        val cr = cornerRadius.toPx()

        fun cornerPath(x0: Float, y0: Float, x1: Float, y1: Float): Path {
            // Dibuja dos segmentos en L desde la esquina hacia dentro del rectángulo
            return Path().apply {
                moveTo(x0, y0 + cl)
                lineTo(x0, y0 + cr)
                arcTo(
                    rect = Rect(x0, y0, x0 + 2 * cr, y0 + 2 * cr),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(x0 + cl, y0)
            }
        }

        // Top-Left
        drawPath(
            path = cornerPath(left, top, right, bottom),
            color = laserColor,
            style = Stroke(width = cs, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Top-Right
        drawPath(
            path = Path().apply {
                moveTo(right - cl, top)
                lineTo(right - cr, top)
                arcTo(
                    rect = Rect(right - 2 * cr, top, right, top + 2 * cr),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(right, top + cl)
            },
            color = laserColor,
            style = Stroke(width = cs, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Bottom-Left
        drawPath(
            path = Path().apply {
                moveTo(left, bottom - cl)
                lineTo(left, bottom - cr)
                arcTo(
                    rect = Rect(left, bottom - 2 * cr, left + 2 * cr, bottom),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(left + cl, bottom)
            },
            color = laserColor,
            style = Stroke(width = cs, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Bottom-Right
        drawPath(
            path = Path().apply {
                moveTo(right - cl, bottom)
                lineTo(right - cr, bottom)
                arcTo(
                    rect = Rect(right - 2 * cr, bottom - 2 * cr, right, bottom),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(right, bottom - cl)
            },
            color = laserColor,
            style = Stroke(width = cs, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Línea de escaneo animada
        val lineY = top + (bottom - top) * lineProgress
        drawLine(
            color = laserColor,
            start = Offset(left + 8.dp.toPx(), lineY),
            end = Offset(right - 8.dp.toPx(), lineY),
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
