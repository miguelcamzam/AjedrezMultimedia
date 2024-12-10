package com.example.ajedrez.userInterface

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.hypot

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
        val cellSize = size.minDimension / 8f  // Puedes ajustar este valor para cambiar el tamaño de las celdas

        // Calcular el número de celdas necesarias para cubrir la pantalla
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
