package com.example.alphakids.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.alphakids.ui.theme.AlphaKidsTealNav
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.AlphaKidsTextGreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Inicio : BottomNavItem("inicio", Icons.Default.Home, "Inicio")
    object Diccionario : BottomNavItem("diccionario", Icons.Default.MenuBook, "Mi Diccionario")
    object Logros : BottomNavItem("logros", Icons.Default.EmojiEvents, "Mis Logros")
}

@Composable
fun AppBottomNavigationBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Inicio,
        BottomNavItem.Diccionario,
        BottomNavItem.Logros
    )

    NavigationBar(
        containerColor = AlphaKidsTealNav
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AlphaKidsTextGreen,
                    selectedTextColor = AlphaKidsTextGreen,
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,

                    indicatorColor = AlphaKidsTealNav
                )
            )
        }
    }
}

@Preview
@Composable
fun AppBottomNavigationBarPreview() {
    AlphakidsTheme {
        AppBottomNavigationBar(currentRoute = BottomNavItem.Inicio.route, onItemSelected = {})
    }
}
