package com.example.ajedrez

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ajedrez.navigation.NavGraph
import com.example.ajedrez.theme.AjedrezTheme
import com.example.ajedrez.viewmodels.GameViewModel
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar OpenCV
        val openCVSuccess = initializeOpenCV()

        // Establecer el contenido de la aplicación si OpenCV se inicializa correctamente
        if (openCVSuccess) {
            setContent {
                AjedrezTheme {
                    val navController = rememberNavController()

                    // Obtener instancia del ViewModel para gestionar el estado del juego
                    val gameViewModel: GameViewModel = viewModel()

                    // Pasar el ViewModel a NavGraph
                    NavGraph(navController = navController, gameViewModel = gameViewModel)
                }
            }
        } else {
            // Manejar el caso si OpenCV no se carga correctamente
            Log.e("MainActivity", "Error al inicializar OpenCV.")
        }
    }

    // Método para inicializar OpenCV y manejar posibles errores
    private fun initializeOpenCV(): Boolean {
        return try {
            val initialized = OpenCVLoader.initDebug()
            if (initialized) {
                Log.d("OpenCV", "OpenCV se ha inicializado correctamente.")
            } else {
                Log.e("OpenCV", "Error al inicializar OpenCV.")
            }
            initialized
        } catch (e: Exception) {
            Log.e("OpenCV", "Excepción al inicializar OpenCV: ${e.message}")
            false
        }
    }
}