package com.example.ajedrez.userInterface

import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ImageViewerScreen(navController: NavController, imageUriString: String) {
    val context = LocalContext.current
    val imageUri = Uri.parse(imageUriString)
    val bitmap = remember {
        MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
    }

    // Variables para el zoom y desplazamiento
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Estado para el gesto transformable
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        offsetX += offsetChange.x
        offsetY += offsetChange.y
        // No necesitamos rotación en este caso
    }

    // Envolver todo en un Box para superponer elementos
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo del tablero de ajedrez
        ChessBoardBackground()

        // Superficie semitransparente
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxSize()
        ) {
            // Contenido principal con imagen ampliable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .transformable(state = state)
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Imagen ampliable",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            }
        }
    }
}