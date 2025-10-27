package com.example.alphakids.ui.screens.camera

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cameraswitch
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphaKidsTextGreen

/**
 * Lightweight camera control row used by the legacy camera screen. While the new OCR
 * screen renders its own controls, other modules still reference this API so we keep
 * the implementation (but forward colours to the new theme constants).
 */
@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    isFlashEnabled: Boolean,
    onClose: () -> Unit,
    onFlipCamera: () -> Unit,
    onToggleFlash: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = onClose,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                contentColor = AlphaKidsTextGreen
            )
        ) {
            Icon(imageVector = Icons.Rounded.Close, contentDescription = "Cerrar cámara")
        }

        FilledIconButton(
            onClick = onFlipCamera,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = AlphaKidsTextGreen,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Rounded.Cameraswitch, contentDescription = "Cambiar cámara")
        }

        FilledTonalIconButton(
            onClick = onToggleFlash,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                contentColor = AlphaKidsTextGreen
            )
        ) {
            Icon(
                imageVector = if (isFlashEnabled) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                contentDescription = "Activar flash"
            )
        }
    }
}
