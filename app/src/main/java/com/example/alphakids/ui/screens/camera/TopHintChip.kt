package com.example.alphakids.ui.screens.camera

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphaKidsTextGreen
import com.example.alphakids.ui.theme.dmSansFamily

/**
 * Simple helper that draws a translucent chip at the top of the camera preview. Older
 * layouts referenced this component directly, so we keep the API stable to prevent build
 * errors when those source files still live in local working copies.
 */
@Composable
fun TopHintChip(
    modifier: Modifier = Modifier,
    text: String
) {
    AssistChip(
        modifier = modifier.padding(8.dp),
        onClick = {},
        enabled = false,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
            disabledLabelColor = AlphaKidsTextGreen
        ),
        label = {
            Text(
                text = text,
                fontFamily = dmSansFamily,
                style = MaterialTheme.typography.labelMedium
            )
        }
    )
}
