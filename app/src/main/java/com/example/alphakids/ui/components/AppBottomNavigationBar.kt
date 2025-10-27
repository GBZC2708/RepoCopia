package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphaKidsTealNav
import com.example.alphakids.ui.theme.AlphaKidsTextGreen

/**
 * Legacy wrapper kept for backwards compatibility with screens that still invoke the
 * old `AppBottomNavigationBar` composable. It simply proxies to the shared
 * [MainBottomBar] while preserving the color palette expected by older code paths.
 */
@Composable
fun AppBottomNavigationBar(
    modifier: Modifier = Modifier,
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    MainBottomBar(
        modifier = modifier.height(72.dp),
        items = items,
        currentRoute = currentRoute,
        onNavigate = onNavigate
    )
}

/**
 * Helper exposing the accent colors that legacy screens were using directly. Modern
 * code should rely on [MaterialTheme], but keeping them here avoids compilation
 * issues on developer machines where stale source files still reference these values.
 */
object LegacyBottomNavColors {
    val container get() = AlphaKidsTealNav
    val content get() = AlphaKidsTextGreen
    val indicator get() = MaterialTheme.colorScheme.primary
}
