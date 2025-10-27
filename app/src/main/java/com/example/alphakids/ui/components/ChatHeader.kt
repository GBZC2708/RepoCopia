package com.example.alphakids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

/**
 * Componente de visualización del icono de chat/lista.
 * Reutiliza la lógica de visualización del icono de WordListItem, pero con colores invertidos.
 */
@Composable
private fun ChatListItemIcon(icon: ImageVector) {
    // Caja del icono: 52x52
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(18.dp))
            // Fondo verde oscuro (Primary)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        // Icono: 32x32
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            // Icono blanco (OnPrimary)
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * Encabezado de la interfaz de chat con el asistente IA.
 */
@Composable
fun ChatHeader(
    modifier: Modifier = Modifier,
    title: String = "Asistente IA",
    subtitle: String = "Recomendaciones inteligentes",
    icon: ImageVector = Icons.Rounded.FlashOn
) {
    // Definimos la forma con radio solo en las esquinas superiores (topStart y topEnd)
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    val fixedHeight = 72.dp // Altura base de 72dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(fixedHeight) // 72dp de altura
            .clip(shape) // Aplicamos la forma para cortar la parte inferior
            // Color de fondo: Verde oscuro (Primary)
            .background(MaterialTheme.colorScheme.primary, shape) // Aplicamos la forma al fondo
            // Padding horizontal 15, vertical 10
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp) // 10dp de gap entre elementos principales
    ) {
        // 1. Icono (Caja 52x52)
        ChatListItemIcon(icon = icon)

        // 2. Textos
        Column(
            // El resto del espacio disponible, centrado verticalmente
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(5.dp), // 5dp de gap entre textos
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp, // Asistente IA -> DM Sans Bold 16
                color = MaterialTheme.colorScheme.onPrimary // Texto blanco
            )
            Text(
                text = subtitle,
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp, // Recomendaciones inteligentes -> DM Sans Regular 14
                color = MaterialTheme.colorScheme.onPrimary // Texto blanco
            )
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, backgroundColor = 0xFFFFC0CB)
@Composable
fun ChatHeaderPreview() {
    AlphakidsTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ChatHeader()
        }
    }
}
