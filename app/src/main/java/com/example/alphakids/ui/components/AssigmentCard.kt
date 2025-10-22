package com.example.alphakids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun AssignmentCard(
    modifier: Modifier = Modifier,
    wordTitle: String,
    wordSubtitle: String,
    chipText: String,
    onClickAssign: () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    // Se eliminó la variable 'totalHeight' y la restricción 'height(totalHeight)'
    // para que la tarjeta se ajuste al contenido (WordListItem + PrimaryButton + padding/gap).

    Column(
        modifier = modifier
            .fillMaxWidth()
            // Se quita la altura fija (.height(totalHeight)) para evitar el recorte del botón
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .padding(all = 15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        WordListItem(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            title = wordTitle,
            subtitle = wordSubtitle,
            icon = Icons.Rounded.Checkroom,
            chipText = chipText,
            isSelected = false,
            onClick = {}
        )

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Asignar",
            icon = Icons.Rounded.Add,
            onClick = onClickAssign
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AssignmentCardPreview() {
    AlphakidsTheme {
        Column(Modifier.padding(16.dp)) {
            AssignmentCard(
                wordTitle = "WORD",
                wordSubtitle = "Categoría",
                chipText = "Chip",
                onClickAssign = {}
            )
        }
    }
}
