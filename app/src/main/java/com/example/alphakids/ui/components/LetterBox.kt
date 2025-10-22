package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun LetterBox(
    modifier: Modifier = Modifier,
    letter: Char? = null,
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 6.dp,
    cornerRadius: Dp = 14.dp,
    color: Color = Color.Black
) {
    val dashLengthPx = LocalDensity.current.run { dashLength.toPx() }
    val strokeWidthPx = LocalDensity.current.run { strokeWidth.toPx() }
    val cornerRadiusPx = LocalDensity.current.run { cornerRadius.toPx() }

    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(dashLengthPx, dashLengthPx),
        phase = 0f
    )

    Box(
        modifier = modifier
            .size(50.dp)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    cornerRadius = CornerRadius(cornerRadiusPx),
                    style = Stroke(
                        width = strokeWidthPx,
                        pathEffect = pathEffect
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (letter != null) {
            Text(
                text = letter.toString(),
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun LetterBoxPreview() {
    AlphakidsTheme {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LetterBox(
                letter = 'A',
                color = MaterialTheme.colorScheme.onBackground
            )
            LetterBox(
                letter = null,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
