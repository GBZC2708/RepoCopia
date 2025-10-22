package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = text,
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    AlphakidsTheme {
        PrimaryButton(
            text = "Iniciar sesión",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonLongTextPreview() {
    AlphakidsTheme {
        PrimaryButton(
            text = "Crear una nueva palabra",
            onClick = {}
        )
    }
}
