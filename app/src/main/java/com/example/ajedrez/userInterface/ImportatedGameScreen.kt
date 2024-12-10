package com.example.ajedrez.userInterface


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import java.io.File
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ImportedGameScreen(navController: NavController, tempDir: File) {
    val images = remember { getImagesFromDirectory(tempDir) }
    val context = LocalContext.current

    var shouldDeleteTempDir by remember { mutableStateOf(false) }

    // Manejar el botón "Atrás"
    BackHandler {
        // Indicar que se debe eliminar el tempDir
        shouldDeleteTempDir = true
        // Navegar hacia atrás
        navController.popBackStack()
    }

    // Si se debe eliminar el tempDir, eliminarlo
    LaunchedEffect(shouldDeleteTempDir) {
        if (shouldDeleteTempDir) {
            tempDir.deleteRecursively()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground()
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(images) { imageFile ->
                    val bitmap = remember {
                        MediaStore.Images.Media.getBitmap(
                            context.contentResolver,
                            Uri.fromFile(imageFile)
                        )
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Imagen importada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 16.dp)
                            .clickable {
                                // Navegar a ImageViewerScreen pasando la ruta de la imagen
                                navController.navigate("imageViewer/${URLEncoder.encode(Uri.fromFile(imageFile).toString(), StandardCharsets.UTF_8.toString())}")
                            }
                    )
                }
            }
        }
    }
}

fun getImagesFromDirectory(directory: File): List<File> {
    return directory.walkTopDown()
        .filter { it.isFile && it.extension.lowercase() in listOf("jpg", "jpeg", "png") }
        .toList()
}

