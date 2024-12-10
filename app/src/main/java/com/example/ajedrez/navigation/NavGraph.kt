package com.example.ajedrez.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ajedrez.userInterface.MainScreen
import com.example.ajedrez.userInterface.LearnScreen
import com.example.ajedrez.game.GameScreen
import com.example.ajedrez.userInterface.IntermediateScreen
import com.example.ajedrez.userInterface.AdvancedScreen
import com.example.ajedrez.game.NameGameScreen
import com.example.ajedrez.game.SelectStarterScreen
import com.example.ajedrez.viewmodels.GameViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.ajedrez.userInterface.GameImagesScreen
import com.example.ajedrez.userInterface.SavedGamesScreen
import java.io.File
import com.example.ajedrez.userInterface.ImportedGameScreen
import com.example.ajedrez.userInterface.ImageViewerScreen
import com.example.ajedrez.userInterface.NativePdfViewer
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun NavGraph(navController: NavHostController, gameViewModel: GameViewModel) {
    NavHost(navController = navController, startDestination = "main") {

        // Pantalla principal con las opciones "Bate a tu rival" y "Aprende a jugar"
        composable("main") {
            MainScreen(
                onNavigateToLearn = { navController.navigate("learn") },
                onNavigateToBattle = { navController.navigate("nameGame") },
                onNavigateToSavedGames = { navController.navigate("savedGames") } // NUEVO CÓDIGO
            )
        }

        // Pantalla de "Aprende a jugar" con las opciones de niveles
        composable("learn") {
            LearnScreen(
                onNavigateToIntermediate = { navController.navigate("intermediate") },
                onNavigateToAdvanced = { navController.navigate("advanced") },
                onNavigateToPDF = { navController.navigate("pdf") }  // Para ver un PDF (principiante)
            )
        }

        // Pantalla para introducir el nombre de la partida
        composable("nameGame") {
            NameGameScreen(
                onGameNameEntered = { navController.navigate("selectStarter") },
                gameViewModel = gameViewModel
            )
        }

        // Pantalla para seleccionar quién empieza la partida
        composable("selectStarter") {
            SelectStarterScreen(
                onRivalStarts = { navController.navigate("battle") },  // Cuando el rival empieza, navega al juego
                onUserStarts = { navController.navigate("battle") },   // Cuando el usuario empieza, navega al juego
                gameViewModel = gameViewModel
            )
        }

        composable("imageViewer/{imageUriString}") { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("imageUriString")
            val imageUriString = encodedUri?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            imageUriString?.let {
                ImageViewerScreen(navController = navController, imageUriString = it)
            }
        }

        // Pantalla de juego "Bate a tu rival!"
        composable("battle") {
            GameScreen(gameViewModel = gameViewModel, navController = navController)
        }

        composable("savedGames") {
            SavedGamesScreen(
                context = LocalContext.current,
                navController = navController,  // Pasamos el navController
                onGameSelected = { selectedGameName ->
                    navController.navigate("gameImages/$selectedGameName")
                }
            )
        }
        composable("gameImages/{gameName}") { backStackEntry ->
            val gameName = backStackEntry.arguments?.getString("gameName")
            gameName?.let {
                GameImagesScreen(
                    context = LocalContext.current,
                    gameName = it,
                    navController = navController  // Agregado navController
                )
            }
        }

        composable("importedGame") {
            // Obtenemos el tempDir desde el SavedGamesScreen a través del ViewModel o NavController
            val tempDirPath = navController.previousBackStackEntry?.savedStateHandle?.get<String>("tempDirPath")
            tempDirPath?.let {
                val tempDir = File(it)
                ImportedGameScreen(navController = navController, tempDir = tempDir)
            }

        }
        // Pantallas individuales para los niveles de aprendizaje
        composable("intermediate") {
            IntermediateScreen()  // Pantalla para nivel intermedio
        }

        composable("advanced") {
            AdvancedScreen()  // Pantalla para nivel avanzado
        }

        // Visualización del PDF en el nivel principiante
        composable("pdf") {
            NativePdfViewer(assetFileName = "AJEDREZ-ELEMENTOS-DE-TACTICA.pdf")
        }


    }
}

