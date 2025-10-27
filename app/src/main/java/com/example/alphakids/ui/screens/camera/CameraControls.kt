package com.example.alphakids.ui.screens.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphaKidsTextGreen
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun CameraControls(
    onTakePhoto: () -> Unit,
    onToggleFlash: () -> Unit,
    onSwitchCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        // Botón de Flash
        IconButton(
            onClick = onToggleFlash,
            modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.3f)).size(56.dp)
        ) {
            Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Flash", tint = Color.White)
        }

        // Botón de Disparo
        IconButton(
            onClick = onTakePhoto,
            modifier = Modifier.size(72.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = AlphaKidsTextGreen)
        ) {
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Tomar foto", tint = Color.White, modifier = Modifier.size(40.dp))
        }

        // Botón de Cambiar Cámara
        IconButton(
            onClick = onSwitchCamera,
            modifier = Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.3f)).size(56.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Cambiar cámara", tint = Color.White)
        }
    }
}

@Preview
@Composable
fun CameraControlsPreview() {
    AlphakidsTheme {
        CameraControls({}, {}, {})
    }
}
