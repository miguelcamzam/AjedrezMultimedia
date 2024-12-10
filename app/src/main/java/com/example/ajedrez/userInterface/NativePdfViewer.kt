package com.example.ajedrez.userInterface

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File

fun copyPdfFromAssetsToInternalStorage(context: Context, assetFileName: String): File {
    val file = File(context.filesDir, assetFileName)
    if (!file.exists()) {
        context.assets.open(assetFileName).use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
    return file
}
@Composable
fun NativePdfViewer(
    assetFileName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val file = remember { copyPdfFromAssetsToInternalStorage(context, assetFileName) }
    var pageCount by remember { mutableStateOf(0) }
    var currentPage by remember { mutableStateOf(0) }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    // Renderizar la página actual del PDF
    LaunchedEffect(currentPage) {
        val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
        renderer.use {
            pageCount = it.pageCount
            val page = it.openPage(currentPage)
            val tempBitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(tempBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            bitmap.value = tempBitmap
        }
    }

    // Fondo general del tablero de ajedrez
    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground() // Fondo de tablero

        // Superficie semitransparente
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Superficie blanca ocupando la mayor parte de la pantalla
                Surface(
                    color = Color.White, // Fondo blanco para el PDF
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Usar toda la altura disponible restante
                    tonalElevation = 4.dp
                ) {
                    bitmap.value?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Controles de navegación de página
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { if (currentPage > 0) currentPage-- }) {
                        Text("Anterior")
                    }
                    Text("Página ${currentPage + 1} de $pageCount")
                    Button(onClick = { if (currentPage < pageCount - 1) currentPage++ }) {
                        Text("Siguiente")
                    }
                }
            }
        }
    }
}
