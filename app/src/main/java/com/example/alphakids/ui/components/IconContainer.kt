package com.example.alphakids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun IconContainer(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String?
) {
    Box(
        modifier = modifier
            .size(84.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun IconContainerPreview() {
    AlphakidsTheme {
        IconContainer(
            icon = Icons.Rounded.Face,
            contentDescription = "Tutor Icon"
        )
    }
}
