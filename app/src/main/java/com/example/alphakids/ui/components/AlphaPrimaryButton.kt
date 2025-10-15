package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun AlphaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Preview(showBackground = true, widthDp = 360)
@Composable
fun AlphaPrimaryButtonPreview() {
    AlphakidsTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AlphaPrimaryButton(
                text = "Iniciar sesión",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            AlphaPrimaryButton(
                text = "Cargando...",
                onClick = {},
                enabled = false
            )
        }
    }
}