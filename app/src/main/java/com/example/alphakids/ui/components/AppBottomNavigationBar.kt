package com.example.alphakids.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.alphakids.ui.theme.AlphaKidsTealNav
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.AlphaKidsTextGreen

@Composable
fun AppBottomNavigationBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            route = "inicio",
            label = "Inicio",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            route = "diccionario",
            label = "Mi Diccionario",
            icon = Icons.Default.MenuBook
        ),
        BottomNavItem(
            route = "logros",
            label = "Mis Logros",
            icon = Icons.Default.EmojiEvents
        )
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
        AppBottomNavigationBar(currentRoute = "inicio", onItemSelected = {})
    }
}
