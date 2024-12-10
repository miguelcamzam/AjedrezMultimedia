package com.example.ajedrez.userInterface

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.File
import androidx.compose.material3.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.io.FileInputStream
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun GameImagesScreen(context: Context, gameName: String, navController: NavController) {
    val images = remember { getGameImages(context, gameName) }
    val coroutineScope = rememberCoroutineScope()
    val exportResult = remember { mutableStateOf<String?>(null) }

    // Launcher para el Storage Access Framework
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let {
            // Iniciar la exportación en un hilo de corrutina
            coroutineScope.launch {
                val success = exportGameAsZip(context, gameName, it)
                if (success) {
                    exportResult.value = "Exportación exitosa"
                } else {
                    exportResult.value = "Error en la exportación"
                }
            }
        }
    }
        ChessBoardBackground()
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Button(
                    onClick = {
                        // Solicitar al usuario el nombre y ubicación para guardar el archivo ZIP
                        createDocumentLauncher.launch("$gameName.zip")
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Exportar partida como ZIP")
                }

                // Mostrar resultado de la exportación
                exportResult.value?.let { resultMessage ->
                    Text(text = resultMessage)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                LazyColumn {
                    items(images) { imageFile ->
                        val bitmap = remember {
                            MediaStore.Images.Media.getBitmap(
                                context.contentResolver,
                                Uri.fromFile(imageFile)
                            )
                        }
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Imagen guardada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(bottom = 16.dp)
                                .clickable {
                                    // Navegar a ImageViewerScreen pasando la ruta de la imagen
                                    navController.navigate(
                                        "imageViewer/${
                                            URLEncoder.encode(
                                                Uri.fromFile(
                                                    imageFile
                                                ).toString(), StandardCharsets.UTF_8.toString()
                                            )
                                        }"
                                    )
                                }
                        )
                    }
                }
            }
        }
}

fun getGameImages(context: Context, gameName: String): List<File> {
    val gameDir = File(context.getExternalFilesDir(null), gameName)
    return gameDir.listFiles()?.filter { it.extension.lowercase() in listOf("jpg", "jpeg", "png") } ?: emptyList()
}

suspend fun exportGameAsZip(context: Context, gameName: String, destinationUri: Uri): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val gameDir = File(context.getExternalFilesDir(null), gameName)
            if (!gameDir.exists() || !gameDir.isDirectory) {
                return@withContext false
            }

            // Obtener el OutputStream del URI proporcionado por el usuario
            context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                ZipOutputStream(outputStream).use { zipOut ->
                    zipFolder(gameDir, gameDir.name, zipOut)
                }
            } ?: return@withContext false

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

// Función para comprimir una carpeta en el ZipOutputStream
fun zipFolder(folder: File, parentFolderName: String, zipOut: ZipOutputStream) {
    val files = folder.listFiles()
    for (file in files) {
        if (file.isDirectory) {
            zipFolder(file, "$parentFolderName/${file.name}", zipOut)
        } else {
            FileInputStream(file).use { fis ->
                val zipEntry = ZipEntry("$parentFolderName/${file.name}")
                zipOut.putNextEntry(zipEntry)
                fis.copyTo(zipOut)
                zipOut.closeEntry()
            }
        }
    }
}
