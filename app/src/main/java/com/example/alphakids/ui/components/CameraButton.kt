package com.example.alphakids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphaKidsTextGreen
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun CameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(AlphaKidsTextGreen)
            .clickable { onClick() }
            .padding(horizontal = 100.dp, vertical = 10.dp), // Padding amplio para hacerlo ancho
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Abrir cámara",
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFF4FBF8)
        )
    }
}

@Preview
@Composable
fun CameraButtonPreview() {
    AlphakidsTheme {
        CameraButton(onClick = {})
    }
}