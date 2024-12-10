package com.example.ajedrez.userInterface

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ajedrez.theme.AjedrezTheme

@Composable
fun AdvancedScreen() {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        ChessBoardBackground()  // Añade el fondo aquí

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Estrategias Avanzadas de Ajedrez")

            Text("1. Ataque Indio de Rey")
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=IJPDH3CQG_k&ab_channel=SissaChess"))
                context.startActivity(intent)
            }) {
                Text("Ver Video Ataque Indio de Rey")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("2. Gambito de Dama")
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=8dEKDbi18fg&ab_channel=Chess.comES"))
                context.startActivity(intent)
            }) {
                Text("Ver Video Gambito de Dama")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdvancedScreenPreview() {
    AjedrezTheme {
        AdvancedScreen()
    }
}
