package com.example.alphakids.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// --- Esquema de colores claro actualizado ---
private val LightColorScheme = lightColorScheme(
    primary = AlphaKidsTextGreen,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    // Colores específicos para la tarjeta
    surfaceVariant = AlphaKidsCardBackground, // Fondo de la tarjeta
    onSurfaceVariant = AlphaKidsTextGreen     // Color del contenido sobre la tarjeta

    /* Otros colores por defecto que puedes sobreescribir
     background = Color(0xFFFFFBFE),
     surface = Color(0xFFFFFBFE),
     onPrimary = Color.White,
     onSecondary = Color.White,
     onTertiary = Color.White,
     onBackground = Color(0xFF1C1B1F),
     onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AlphakidsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}