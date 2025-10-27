package com.example.alphakids.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun MainBottomBar(
    modifier: Modifier = Modifier,
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    BottomNavIcon(
                        icon = item.icon,
                        isSelected = isSelected
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentBottomBarPreview() {
    val studentItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("dictionary", "Mi Diccionario", Icons.Rounded.Book),
        BottomNavItem("achievements", "Mis Logros", Icons.Rounded.WorkspacePremium)
    )

    AlphakidsTheme {
        MainBottomBar(
            items = studentItems,
            currentRoute = "home",
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StudentBottomBarPreviewSelected() {
    val studentItems = listOf(
        BottomNavItem("home", "Inicio", Icons.Rounded.Home),
        BottomNavItem("dictionary", "Mi Diccionario", Icons.Rounded.Book),
        BottomNavItem("achievements", "Mis Logros", Icons.Rounded.WorkspacePremium)
    )

    AlphakidsTheme {
        MainBottomBar(
            items = studentItems,
            currentRoute = "dictionary",
            onNavigate = {}
        )
    }
}
