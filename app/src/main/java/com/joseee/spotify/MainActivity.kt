package com.joseee.spotify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.joseee.spotify.ui.theme.SpotifyTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

//Composable principal: Maneja las pantallas y como se muestran
@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            PantallaLogin(navController)
        }
        composable("principal") {
            PantallaPrincipal(navController)
            BarraDeNavegacion()
        }
    }
}


//Pantalla del login
@Composable
fun PantallaLogin(navController: NavController) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Inicio de Sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (usuario == "admin" && contrasena == "1234") {
                navController.navigate("principal") // redirige a otra pantalla
            } else {
                mensaje = "Usuario o contraseña incorrectos"
            }
        }) {
            Text("Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = mensaje)
    }
}

//Pantalla principal (Creación de playlists)
@Composable
fun PantallaPrincipal(navController: NavController) {

    var nombrePlaylist by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Creación de playlists", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = nombrePlaylist,
            onValueChange = { nombrePlaylist = it },
            label = { Text("Ingrese el nombre de la playlist") }
        )
        Button(onClick = {print("hola")}){
            Text("Crear")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {navController.navigate("login")}
        ){
            Text("Regresar al inicio de sesión")
        }
    }

}

//Barra de navegación presente en toda la app
@Composable
fun BarraDeNavegacion() {

    val tabs = listOf("Playlists", "Reproducir")
    val selectedTab = remember { mutableStateOf(0) }

    TabRow(selectedTabIndex = selectedTab.value) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab.value == index,
                onClick = { selectedTab.value = index },
                text = { Text(text = title) },
            )
        }
    }
}


//Vistas previas
@Preview(showBackground = true)
@Composable
fun Preview() {
    App()
}