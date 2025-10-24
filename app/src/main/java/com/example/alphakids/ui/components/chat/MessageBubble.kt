package com.example.alphakids.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.domain.models.Word
import com.example.alphakids.ui.chat.ChatMessage
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun MessageBubble(
    message: ChatMessage,
    onAssignWord: ((Word) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
        ) {
            // Burbuja del mensaje
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                            bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (message.isFromUser) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (message.isFromUser) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontFamily = dmSansFamily,
                    lineHeight = 20.sp
                )
            }
            
            // Botón de asignar palabra si hay recomendación
            message.recommendedWord?.let { word ->
                if (!message.isFromUser && onAssignWord != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onAssignWord(word) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Asignar '${word.texto}'",
                            fontSize = 12.sp,
                            fontFamily = dmSansFamily,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Timestamp
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestamp(message.timestamp),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontFamily = dmSansFamily
            )
        }
        
        if (message.isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Ahora"
        diff < 3600_000 -> "${diff / 60_000}m"
        diff < 86400_000 -> "${diff / 3600_000}h"
        else -> "${diff / 86400_000}d"
    }
}