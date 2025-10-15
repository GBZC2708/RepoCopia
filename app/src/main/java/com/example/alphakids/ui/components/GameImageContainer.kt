package com.example.alphakids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphaKidsTealLight
import com.example.alphakids.ui.theme.AlphaKidsTextGreen
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun GameImageContainer(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(AlphaKidsTealLight)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Imagen del juego",
            modifier = Modifier.size(128.dp),
            tint = AlphaKidsTextGreen
        )
    }
}

@Preview
@Composable
fun GameImageContainerPreview() {
    AlphakidsTheme {
        GameImageContainer(icon = Icons.Default.Checkroom)
    }
}