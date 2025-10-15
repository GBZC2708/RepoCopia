package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphaKidsTealLight
import com.example.alphakids.ui.theme.AlphaKidsTextGreen
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun DifficultyChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = AlphaKidsTealLight
    ) {
        Text(
            text = text,
            color = AlphaKidsTextGreen,
            fontSize = 10.sp,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Preview
@Composable
fun DifficultyChipPreview() {
    AlphakidsTheme {
        DifficultyChip(text = "Fácil")
    }
}