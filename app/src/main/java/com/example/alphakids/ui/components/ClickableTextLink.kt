package com.example.alphakids.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun ClickableTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier.clickable { onClick() },
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary
    )
}


@Preview(showBackground = true)
@Composable
fun ClickableTextLinkPreview() {
    AlphakidsTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ClickableTextLink(
                text = "¿Olvidaste tu contraseña?",
                onClick = {}
            )
        }
    }
}