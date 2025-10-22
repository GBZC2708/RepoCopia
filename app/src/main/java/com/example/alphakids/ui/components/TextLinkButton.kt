package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
fun TextLinkButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TextLinkButtonPreview() {
    AlphakidsTheme {
        TextLinkButton(
            text = "¿Olvidaste tu contraseña?",
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TextLinkButtonRegisterPreview() {
    AlphakidsTheme {
        TextLinkButton(
            text = "Regístrate aquí",
            onClick = {}
        )
    }
}