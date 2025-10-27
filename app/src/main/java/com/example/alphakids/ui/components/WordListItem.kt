package com.example.alphakids.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    chipText: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    imageUrl: String? = null
) {
    // 🔍 LOGGING: Verificar qué llega al componente
    Log.d("WordListItem", "=== RENDERING WORD ===")
    Log.d("WordListItem", "Title: $title")
    Log.d("WordListItem", "ImageUrl: ${imageUrl ?: "NULL/EMPTY"}")
    Log.d("WordListItem", "ImageUrl Length: ${imageUrl?.length ?: 0}")

    val shape = RoundedCornerShape(28.dp)

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ListItemIcon(icon = icon, imageUrl = imageUrl)
                Column {
                    Text(
                        text = title,
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = subtitle.ifEmpty { "Sin categoría" },
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            InfoChip(
                text = chipText.ifEmpty { "Normal" },
                isSelected = true
            )
        }
    }
}

@Composable
private fun ListItemIcon(icon: ImageVector, imageUrl: String? = null) {
    val size = 52.dp
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUrl.isNullOrBlank() -> {
                // Imagen vacía o nula
                Log.d("ListItemIcon", "❌ URL vacía/nula - Mostrando icono por defecto")
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            else -> {
                // Intentar cargar imagen
                Log.d("ListItemIcon", "🔄 Intentando cargar: $imageUrl")
                
                // Limpiamos la URL de posibles caracteres adicionales
                val cleanUrl = cleanImageUrl(imageUrl)
                Log.d("ListItemIcon", "URL limpia: $cleanUrl")
                
                // Verificar si la URL es válida
                if (!cleanUrl.startsWith("http")) {
                    Log.e("ListItemIcon", "❌ URL inválida: $cleanUrl")
                    Icon(
                        imageVector = Icons.Rounded.Image,
                        contentDescription = "URL inválida",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    return@Box
                }

                // Configuración de Coil para cargar la imagen
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(cleanUrl)
                        .crossfade(true)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "Imagen de palabra",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Log.d("ListItemIcon", "Cargando imagen...")
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    error = { error ->
                        Log.e("ListItemIcon", "ERROR al cargar imagen")
                        Log.e("ListItemIcon", "URL: $cleanUrl")
                        Log.e("ListItemIcon", "Error: ${error.result.throwable?.message}")
                        Log.e("ListItemIcon", "Cause: ${error.result.throwable?.cause}")
                        error.result.throwable?.printStackTrace()

                        // Mostrar icono de error
                        Icon(
                            imageVector = Icons.Rounded.Image,
                            contentDescription = "Error al cargar",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    success = { success ->
                        Log.d("ListItemIcon", "✅ Imagen cargada exitosamente")
                        // No necesitamos envolver el painter en un Box adicional
                        // El SubcomposeAsyncImage ya maneja la visualización correctamente
                    }
                )
            }
        }
    }
}

/**
 * Función de utilidad para limpiar URLs de imágenes
 * Elimina caracteres especiales, espacios en blanco y saltos de línea
 */
private fun cleanImageUrl(url: String?): String {
    if (url.isNullOrBlank()) return ""
    
    return url.trim()
        .replace("`", "")
        .replace("\\s".toRegex(), "") // Eliminar espacios en blanco
        .replace("\n", "") // Eliminar saltos de línea
        .replace("\\\\".toRegex(), "") // Eliminar barras invertidas
}
