package com.example.alphakids.ui.components

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Shared bottom navigation definition. Extracted into its own file so both the legacy
 * bottom bar (AppBottomNavigationBar) and the modern MainBottomBar reuse the same
 * public constructor without triggering duplicate class definitions on case-insensitive
 * file systems.
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
