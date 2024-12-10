package com.example.ajedrez.game

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ajedrez.viewmodels.GameViewModel
import java.io.File
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import java.io.FileOutputStream
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.common.util.concurrent.ListenableFuture
import kotlin.math.hypot

enum class Turn {
    USER,
    RIVAL
}

@Composable
fun GameScreen(gameViewModel: GameViewModel, navController: NavController) {
    var currentTurn by remember { mutableStateOf(Turn.USER) }
    var proposedMove by remember { mutableStateOf<String?>(null) }
    var rivalMoveProcessed by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var capturedImageFile by remember { mutableStateOf<File?>(null) }
    val context = LocalContext.current

    val gameName = gameViewModel.gameName.observeAsState().value
    val gameFolder = File(context.getExternalFilesDir(null), gameName ?: "default_game")

    val imageCapture = remember { ImageCapture.Builder().build() }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Manejo de permisos
    val cameraPermissionGranted = remember { mutableStateOf(false) }

    // Permisos de la camara
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        cameraPermissionGranted.value = isGranted
    }
    LaunchedEffect(Unit) {
        val permissionResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionResult == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            cameraPermissionGranted.value = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Fondo
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo del patrón de ajedrez rotado
        ChessBoardBackground()

        if (showCamera && cameraPermissionGranted.value) {
            // Mostrar la vista previa de la cámara
            CameraPreview(
                imageCapture = imageCapture,
                cameraProviderFuture = cameraProviderFuture,
                lifecycleOwner = lifecycleOwner,
                modifier = Modifier.fillMaxSize()
            )
            // Botón para capturar la foto
            Button(
                onClick = {
                    capturePhoto(context, imageCapture, gameFolder) { file ->
                        capturedImageFile = file
                        showCamera = false
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Capturar")
            }
        } else {
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (currentTurn) {
                    Turn.USER -> {
                        if (isProcessing) {
                            Text("La IA está preparando el movimiento...")

                            LaunchedEffect(Unit) {
                                delay(2000)
                                proposedMove = "e2-e4"
                                isProcessing = false
                            }
                        } else {
                            if (proposedMove != null) {
                                Text("Propuesta de movimiento por la IA: $proposedMove")
                                Spacer(modifier = Modifier.height(16.dp))

                                Button(onClick = {
                                    currentTurn = Turn.RIVAL
                                    proposedMove = null
                                    rivalMoveProcessed = false
                                }) {
                                    Text("Aceptar y continuar (Turno del Rival)")
                                }
                            } else {
                                Text("Es tu turno, toma o selecciona una imagen.")

                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    if (cameraPermissionGranted.value) {
                                        showCamera = true
                                    } else {
                                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }) {
                                    Text("Tomar Foto")
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { /* Implementa la selección de imagen desde la galería si lo deseas */ }) {
                                    Text("Seleccionar Imagen de la Galería")
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                capturedImageFile?.let { file ->
                                    val bitmap = remember {
                                        BitmapFactory.decodeFile(file.absolutePath)
                                    }
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = "Imagen capturada",
                                        modifier = Modifier.size(200.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    isProcessing = true
                                }) {
                                    Text("Aceptar y procesar mi jugada")
                                }
                            }
                        }
                    }

                    Turn.RIVAL -> {
                        if (isProcessing) {
                            Text("Procesando el movimiento del rival...")

                            LaunchedEffect(Unit) {
                                delay(2000)
                                proposedMove = "e7-e5"
                                isProcessing = false
                            }
                        } else if (rivalMoveProcessed) {
                            Text("La IA ha procesado el movimiento del rival.")
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                                currentTurn = Turn.USER
                                rivalMoveProcessed = false
                            }) {
                                Text("Aceptar y continuar (Tu turno)")
                            }
                        } else {
                            Text("Es el turno del rival. Toma o selecciona una imagen.")
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(onClick = {
                                if (cameraPermissionGranted.value) {
                                    showCamera = true
                                } else {
                                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }) {
                                Text("Tomar Foto del Movimiento del Rival")
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { /* Implementa la selección de imagen desde la galería si lo deseas */ }) {
                                Text("Seleccionar Imagen del Movimiento del Rival")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            capturedImageFile?.let { file ->
                                val bitmap = remember {
                                    BitmapFactory.decodeFile(file.absolutePath)
                                }
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Imagen capturada",
                                    modifier = Modifier.size(200.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                isProcessing = true
                                rivalMoveProcessed = true
                            }) {
                                Text("Aceptar y procesar movimiento del rival")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { showExitDialog = true }) {
                    Text("Salir de la Partida")
                }
            }

            // Confirmar output
            if (showExitDialog) {
                ExitGameDialog(
                    onDismiss = { showExitDialog = false },
                onConfirmSave = {
                    // Usuario elige guardar
                    showExitDialog = false
                    Toast.makeText(context, "Partida guardada.", Toast.LENGTH_SHORT).show()
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onConfirmDiscard = {
                    // Usuario elige no guardar, eliminar carpeta
                    deleteGameFolder(gameFolder)
                    showExitDialog = false
                    Toast.makeText(context, "No se ha guardado la partida.", Toast.LENGTH_SHORT).show()
                    navController.navigate("main") {
                        popUpTo("main") { inclusive = true }
                    }
                }
                )
            }
        }
    }
}


//  eliminar la carpeta de la partida
fun deleteGameFolder(gameFolder: File) {
    if (gameFolder.exists()) {
        gameFolder.deleteRecursively()
    }
}

// Composable personalizado para el diálogo de salida
@Composable
fun ExitGameDialog(
    onDismiss: () -> Unit,
    onConfirmSave: () -> Unit,
    onConfirmDiscard: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Salir de la partida", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("¿Deseas guardar la partida antes de salir?")
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onConfirmDiscard) {
                        Text("No guardar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onConfirmSave) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    gameFolder: File,
    onPhotoCaptured: (File) -> Unit
) {
    val file = File(gameFolder, "captured_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Comprimir la imagen después de guardarla
                compressImage(file)
                onPhotoCaptured(file) // Notificar que la foto se ha capturado
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Error al capturar la foto: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

// Comprimir imagenes
fun compressImage(file: File) {
    try {
        val originalBitmap = BitmapFactory.decodeFile(file.absolutePath)
        val outputStream = FileOutputStream(file)

        // Comprimir y sobrescribir el archivo existente
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

        outputStream.flush()
        outputStream.close()
        originalBitmap.recycle() // Liberar memoria
    } catch (e: Exception) {
        e.printStackTrace()
    }
}




@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            // Configurar la cámara
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                try {
                    cameraProvider.unbindAll()

                    // Vincular la cámara con el ciclo de vida
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        }
    )
}


@Composable
fun ChessBoardBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRotatedChessPattern()
    }
}

fun DrawScope.drawRotatedChessPattern() {
    // Definir los colores del patrón
    val lightColor = Color(0xFFF0D9B5)
    val darkColor = Color(0xFFB58863)

    // Rotar el canvas
    val rotationAngle = 30f  // Ajusta este valor para cambiar el ángulo de rotación
    rotate(rotationAngle) {
        // Calcular el tamaño de las celdas
        val cellSize = size.minDimension / 8f  // Ajusta este valor para cambiar el tamaño de las celdas

        // Calcular el numero de celdas necesarias para cubrir la pantalla
        val diagonal = hypot(size.width, size.height)
        val numCellsX = (diagonal / cellSize).toInt() + 1
        val numCellsY = (diagonal / cellSize).toInt() + 1

        // Dibujar el patrón de cuadrados
        for (i in -numCellsX..numCellsX) {
            for (j in -numCellsY..numCellsY) {
                val color = if ((i + j) % 2 == 0) lightColor else darkColor
                drawRect(
                    color = color,
                    topLeft = Offset(i * cellSize, j * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }
        }
    }
}
