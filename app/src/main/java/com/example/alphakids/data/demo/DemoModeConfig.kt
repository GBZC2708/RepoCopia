package com.example.alphakids.data.demo

import com.example.alphakids.BuildConfig

/**
 * Centraliza el flag de modo demo para poder habilitar o deshabilitar
 * los repositorios basados en datos mock sin esparcir referencias a
 * [BuildConfig] en todo el código.
 */
object DemoModeConfig {
    val isEnabled: Boolean = BuildConfig.DEMO_MODE
}

