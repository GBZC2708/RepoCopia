package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphaKidsTealLight
import com.example.alphakids.ui.theme.AlphaKidsTextGreen
import com.example.alphakids.ui.theme.dmSansFamily

/**
 * Maintains the original difficulty chip visuals requested before the component
 * consolidation. Future code should adopt [InfoChip], but we keep this version so older
 * files referenced by the Windows clone keep compiling.
 */
@Composable
fun DifficultyChip(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val chipColors = AssistChipDefaults.assistChipColors(
        containerColor = if (selected) AlphaKidsTealLight else MaterialTheme.colorScheme.surfaceVariant,
        labelColor = if (selected) AlphaKidsTextGreen else MaterialTheme.colorScheme.onSurfaceVariant
    )

    AssistChip(
        modifier = modifier,
        colors = chipColors,
        onClick = onClick ?: {},
        enabled = onClick != null,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = dmSansFamily,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    )
}
