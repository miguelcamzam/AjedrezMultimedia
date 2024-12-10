package com.example.ajedrez.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ajedrez.userInterface.ChessBoardBackground
import com.example.ajedrez.viewmodels.GameViewModel

@Composable
fun SelectStarterScreen(
    onRivalStarts: () -> Unit,  // Cuando empieza el rival
    onUserStarts: () -> Unit,   // Cuando empieza el usuario
    gameViewModel: GameViewModel  // Asegúrate de tener este parámetro
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("¿Quién empieza la partida?")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                gameViewModel.setPlayerStartsFirst(false)  // Rival empieza
                onRivalStarts()  // Navegar a la pantalla de batalla
            }) {
                Text("Empieza mi rival")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                gameViewModel.setPlayerStartsFirst(true)  // Usuario empieza
                onUserStarts()  // Navegar a la pantalla de batalla
            }) {
                Text("Empiezo yo")
            }
        }
    }
}
