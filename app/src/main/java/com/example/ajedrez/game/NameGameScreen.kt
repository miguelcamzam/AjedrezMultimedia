package com.example.ajedrez.game

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ajedrez.viewmodels.GameViewModel
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import java.io.File
import androidx.compose.ui.platform.LocalContext

@Composable
fun NameGameScreen(onGameNameEntered: () -> Unit, gameViewModel: GameViewModel) {
    var gameName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground()
        // Opcional: Surface semitransparente para mejorar la legibilidad
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Introduce el nombre de la partida")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = gameName,
                    onValueChange = { gameName = it },
                    label = { Text("Nombre de la partida") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (gameName.isNotBlank()) {
                        val gameFolder = File(context.getExternalFilesDir(null), gameName)

                        if (!gameFolder.exists()) {
                            val created = gameFolder.mkdirs()
                            if (created) {
                                Toast.makeText(context, "Carpeta creada: ${gameFolder.absolutePath}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al crear la carpeta.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                        }

                        // Llamar a enforceMaxSavedGames aquí
                        enforceMaxSavedGames(context, maxGames = 5)

                        gameViewModel.setGameName(gameName)
                        onGameNameEntered()  // Navegar a la siguiente pantalla
                    } else {
                        Toast.makeText(context, "El nombre de la partida no puede estar vacío.", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Confirmar nombre")
                }
            }
        }
    }
}

// Función para eliminar las partidas más antiguas y mantener solo las últimas 5
fun enforceMaxSavedGames(context: Context, maxGames: Int) {
    val baseDir = context.getExternalFilesDir(null)
    val gameFolders = baseDir?.listFiles()?.filter { it.isDirectory }

    if (gameFolders != null && gameFolders.size > maxGames) {
        val sortedGameFolders = gameFolders.sortedBy { it.lastModified() }
        val foldersToDelete = sortedGameFolders.take(gameFolders.size - maxGames)

        for (folder in foldersToDelete) {
            folder.deleteRecursively()
        }
    }
}
