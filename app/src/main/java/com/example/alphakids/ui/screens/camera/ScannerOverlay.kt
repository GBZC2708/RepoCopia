package com.example.alphakids.ui.screens.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphaKidsTextGreen
import com.example.alphakids.ui.theme.dmSansFamily

/**
 * Semi-transparent overlay with a rounded rectangle ROI used by the standalone camera
 * screen. We keep it minimal to avoid conflicts with the newer OCR overlay, but offer a
 * matching API so legacy callers can continue rendering hints.
 */
@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    roiWidthFraction: Float = 0.75f,
    roiHeightFraction: Float = 0.3f,
    strokeWidth: Dp = 4.dp,
    hint: String? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width * roiWidthFraction
            val height = size.height * roiHeightFraction
            val left = (size.width - width) / 2f
            val top = (size.height - height) / 2f

            drawRect(color = Color.Black.copy(alpha = 0.55f), size = size)

            drawRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(width, height),
                blendMode = BlendMode.Clear
            )

            drawRoundRect(
                color = AlphaKidsTextGreen,
                topLeft = Offset(left, top),
                size = Size(width, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(32f, 32f),
                style = Stroke(width = strokeWidth.toPx())
            )
        }

        if (!hint.isNullOrBlank()) {
            Text(
                text = hint,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontFamily = dmSansFamily
            )
        }
    }
}
