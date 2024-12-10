package com.example.ajedrez.userInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToBattle: () -> Unit,
    onNavigateToSavedGames: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground() // Nuevo fondo

        // Ajusta la opacidad o comenta este Box
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.3f)) // Reduce la opacidad
        )

        // Contenido de la pantalla
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Button(onClick = onNavigateToBattle, modifier = Modifier.padding(8.dp)) {
                Text(text = "¡Bate a tu rival!")
            }
            Button(onClick = onNavigateToLearn, modifier = Modifier.padding(8.dp)) {
                Text(text = "¡Aprende a jugar!")
            }
            Button(onClick = onNavigateToSavedGames, modifier = Modifier.padding(8.dp)) {
                Text(text = "Ver partidas guardadas")
            }
        }
    }
}
