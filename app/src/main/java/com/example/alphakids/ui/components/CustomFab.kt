package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFAB(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    // --- NUEVOS PARÁMETROS ---
    containerSize: Dp = 52.dp,
    iconSize: Dp = 32.dp,
    shape: Shape = RoundedCornerShape(18.dp)
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(containerSize),
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview
@Composable
fun CustomFABPreview() {
    AlphakidsTheme {
        CustomFAB(
            icon = Icons.Default.Settings,
            contentDescription = "Configuración",
            onClick = {}
        )
    }
}
