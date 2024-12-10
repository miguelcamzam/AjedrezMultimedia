package com.example.ajedrez.userInterface

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers

@Composable
fun SavedGamesScreen(context: Context, navController: NavController, onGameSelected: (String) -> Unit) {
    val savedGames = remember { getSavedGames(context) }
    val coroutineScope = rememberCoroutineScope()
    var importedGameTempDir by remember { mutableStateOf<File?>(null) }

    // Launcher para seleccionar un archivo ZIP
    val importZipLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                val tempDir = importZipFile(context, it)
                if (tempDir != null) {
                    importedGameTempDir = tempDir
                    // Guardar tempDir en savedStateHandle
                    navController.currentBackStackEntry?.savedStateHandle?.set("tempDirPath", tempDir.absolutePath)
                    // Navegar a la pantalla que muestra las imágenes importadas
                    navController.navigate("importedGame")
                } else {
                    // Manejar el error al importar el ZIP
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground()
        // Opcional: Surface semitransparente para mejorar la legibilidad
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Button(
                    onClick = {
                        // Lanzar el selector de archivos para importar un ZIP
                        importZipLauncher.launch(arrayOf("application/zip"))
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Importar partida ZIP")
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(savedGames) { gameName ->
                        Button(
                            onClick = { onGameSelected(gameName) },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text(text = gameName)
                        }
                    }
                }
            }
        }
    }
}

// Función para obtener las partidas guardadas
fun getSavedGames(context: Context): List<String> {
    val baseDir = context.getExternalFilesDir(null)
    val gameFolders = baseDir?.listFiles()?.filter { it.isDirectory }

    return if (gameFolders != null) {
        val sortedGameFolders = gameFolders.sortedByDescending { it.lastModified() }
        val recentGameFolders = sortedGameFolders.take(5)
        recentGameFolders.map { it.name }
    } else {
        emptyList()
    }
}

// Función para importar y extraer el ZIP
suspend fun importZipFile(context: Context, zipUri: Uri): File? {
    return withContext(Dispatchers.IO) {
        try {
            val tempDir = File(context.cacheDir, "imported_game_${System.currentTimeMillis()}")
            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }

            context.contentResolver.openInputStream(zipUri)?.use { inputStream ->
                unzip(inputStream, tempDir)
            } ?: return@withContext null

            return@withContext tempDir
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun unzip(zipInputStream: InputStream, targetDirectory: File) {
    ZipInputStream(BufferedInputStream(zipInputStream)).use { zis ->
        var ze: ZipEntry?
        val buffer = ByteArray(1024)
        while (zis.nextEntry.also { ze = it } != null) {
            val file = File(targetDirectory, ze!!.name)
            val dir = if (ze!!.isDirectory) file else file.parentFile
            if (!dir.exists() && !dir.mkdirs()) {
                throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)
            }
            if (ze!!.isDirectory) continue
            FileOutputStream(file).use { fout ->
                var count: Int
                while (zis.read(buffer).also { count = it } != -1) {
                    fout.write(buffer, 0, count)
                }
            }
        }
    }
}