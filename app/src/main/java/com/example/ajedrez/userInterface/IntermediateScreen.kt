package com.example.ajedrez.userInterface

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ajedrez.theme.AjedrezTheme

@Composable
fun IntermediateScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground()  // Fondo de ajedrez

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Estrategias Intermedias de Ajedrez")


            Text("1. Apertura Italiana")
            EmbeddedYoutubeVideo("https://www.youtube.com/watch?v=rVYzyABmDLU")

            Spacer(modifier = Modifier.height(16.dp))

            Text("2. Defensa Siciliana")
            EmbeddedYoutubeVideo("https://www.youtube.com/watch?v=r2g5eYCOrKo")
        }
    }
}

// Función para integrar un video de YouTube en WebView
@Composable
fun EmbeddedYoutubeVideo(videoUrl: String) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),  // Establecer el tamaño del video
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(videoUrl)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun IntermediateScreenPreview() {
    AjedrezTheme {
        IntermediateScreen()
    }
}
