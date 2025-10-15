package com.example.alphakids.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun LetterInputBox(
    letter: Char?,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .size(width = 50.dp, height = 60.dp)
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (letter != null) {
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}


@Composable
fun DashedLetterInputBox(
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    androidx.compose.foundation.Canvas(
        modifier = modifier.size(width = 50.dp, height = 60.dp)
    ) {
        drawRoundRect(
            color = Color.LightGray,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LetterInputBoxPreview() {
    AlphakidsTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LetterInputBox(letter = 'A')
            DashedLetterInputBox()
            DashedLetterInputBox()
            DashedLetterInputBox()
        }
    }
}