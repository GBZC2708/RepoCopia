package com.example.alphakids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

/**
 * Botón compacto con un icono, usado específicamente para el campo de chat.
 * Adapta la forma y los colores del PrimaryButton.
 */
@Composable
private fun SendButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // La forma debe coincidir con el componente contenedor (28.dp)
    val shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxHeight()
            .width(56.dp) // Ancho fijo para el área del botón
            .clip(shape),
        shape = shape,
        // Eliminamos el padding para centrar el icono en la altura completa
        contentPadding = ButtonDefaults.ContentPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary, // Color verde oscuro
            contentColor = MaterialTheme.colorScheme.onPrimary, // Icono blanco
        )
    ) {
        // Icono 12x12
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Enviar mensaje",
            modifier = Modifier.size(12.dp)
        )
    }
}

/**
 * Componente de entrada de mensaje/chat que contiene un campo de texto y un botón de envío.
 */
@Composable
fun ChatInputBar(
    modifier: Modifier = Modifier,
    placeholderText: String = "Escribe un mensaje",
    onSendClick: () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            // Utilizamos IntrinsicSize.Min para que el Row determine su altura en base al contenido,
            // que en este caso será la altura del SendButton.
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape), // Color de fondo del campo de texto
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Campo de texto (simulado con un Box/Text por simplicidad, se usaría BasicTextField o OutlinedTextField)
        Box(
            modifier = Modifier
                .weight(1f) // Ocupa el espacio restante
                .fillMaxHeight()
                .padding(horizontal = 24.dp), // Padding para el texto
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = placeholderText,
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp, // Tamaño de texto 10.sp
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) // Color de placeholder
            )
            // Aquí iría el BasicTextField real en una implementación completa.
        }

        // 2. Botón de envío
        SendButton(
            onClick = onSendClick
        )
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, backgroundColor = 0xFFF0F0FF)
@Composable
fun ChatInputBarPreview() {
    AlphakidsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ChatInputBar(
                onSendClick = {}
            )
        }
    }
}
