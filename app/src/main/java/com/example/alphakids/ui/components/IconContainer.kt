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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun IconContainer(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String?,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .size(84.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(64.dp),
            tint = contentColor
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

@Preview
@Composable
fun IconContainerErrorPreview() {
    AlphakidsTheme {
        IconContainer(
            icon = Icons.Rounded.Face,
            contentDescription = "Error Icon",
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.error
        )
    }
}
