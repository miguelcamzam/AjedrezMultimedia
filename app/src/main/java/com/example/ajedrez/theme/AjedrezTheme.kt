package com.example.ajedrez.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AjedrezTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // Detecta el tema del sistema
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        // Colores para tema oscuro
        MaterialTheme.colorScheme.copy(
            primary = DarkPrimary,
            onPrimary = DarkOnPrimary,
            background = DarkBackground
        )
    } else {
        // Colores para tema claro
        MaterialTheme.colorScheme.copy(
            primary = LightPrimary,
            onPrimary = LightOnPrimary,
            background = LightBackground
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AjedrezTypography,  // Tipografía personalizada
        content = content
    )
}
