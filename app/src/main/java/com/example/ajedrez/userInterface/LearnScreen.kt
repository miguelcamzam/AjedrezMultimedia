package com.example.ajedrez.userInterface

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LearnScreen(onNavigateToIntermediate: () -> Unit, onNavigateToAdvanced: () -> Unit, onNavigateToPDF: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground() // Fondo del tablero de ajedrez

        // Contenido de la pantalla
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center, // Centrar los botones verticalmente
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Button(onClick = onNavigateToPDF, modifier = Modifier.padding(8.dp)) {
                Text(text = "Principiante")
            }
            Button(onClick = onNavigateToIntermediate, modifier = Modifier.padding(8.dp)) {
                Text(text = "Intermedio")
            }
            Button(onClick = onNavigateToAdvanced, modifier = Modifier.padding(8.dp)) {
                Text(text = "Avanzado")
            }
        }
    }
}
