package com.joseee.spotify

import android.os.Bundle
import android.service.autofill.OnClickAction
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.joseee.spotify.ui.theme.SpotifyTheme
import java.net.URLEncoder
import java.util.UUID


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // Crea el controlador de navegación
                val navController = rememberNavController()

                // Lista de playlists con estado
                val playlists = remember { mutableStateListOf<Playlist>() }

                // Llama a la función que maneja la navegación
                App(navController, playlists)
            }
        }
    }
}

// ESTRUCTURAS DE DATOS PARA LAS CANCIONES Y LAS PLAYLISTS
data class Cancion(
    val nombre: String,
    val artista: String,
    val duracion: String // Ej. "3:45"
)

data class Playlist(
    val id: String = UUID.randomUUID().toString(),
    var nombre: String,
    val canciones: MutableList<Cancion> = mutableListOf()
)

const val RUTA_EDICION = "pantallaEdicionPlaylists"

//Funcion de ruta para edicion de playlists
fun rutaEdicionConNombre(nombre: String): String = "$RUTA_EDICION/${URLEncoder.encode(nombre, "UTF-8")}"

//COMPONENTES DE LA APLICACION

//Composable principal: Maneja las pantallas y como se muestran
@Composable
fun App(navController: NavHostController, playlists: SnapshotStateList<Playlist>) {

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            PantallaLogin(navController)
        }
        composable("principal") {
            PantallaPrincipal(navController, playlists)
        }

        //Este composable se encarga de asignar una ruta distinta para cada playlist existente
        composable(
            "$RUTA_EDICION/{nombre}",
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            PantallaEdicionPlaylists(nombre, playlists, navController)
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
fun PantallaPrincipal(navController: NavHostController, playlists: SnapshotStateList<Playlist>) {

    var nombrePlaylist by remember { mutableStateOf("") }


    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Creación de playlists", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = nombrePlaylist,
            onValueChange = { nombrePlaylist = it },
            label = { Text("Ingrese el nombre de la playlist") }
        )
        Button(onClick = {
            //Valida que el campo no este vacio
            if (nombrePlaylist.isNotBlank()) {
                playlists.add(Playlist(nombre = nombrePlaylist))
                nombrePlaylist = ""
            }
        }){
            Text("Crear")
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Lista de playlists (Cards) con acceso a su respectivo espacio
        LazyColumn {
            items(playlists) { playlist ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(playlist.nombre)
                        Button(onClick = {
                            navController.navigate(rutaEdicionConNombre(playlist.nombre))
                        }) {
                            Text("Editar")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

    }

}

@Composable
fun PantallaEdicionPlaylists(nombre: String, playlists: SnapshotStateList<Playlist>,navController: NavHostController) {
    val playlist = playlists.find { it.nombre == nombre }

    if (playlist == null) {
        Text("Playlist no encontrada")
        return
    }
    Button(
        onClick = ({navController.navigate("principal")})
    ) { Text("Regresar")}

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize().background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Editando: ${playlist.nombre}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = ({})
        ) {
            Text("Agregar una canción")
        }
        // Aquí puedes agregar o editar canciones
        playlist.canciones.forEach { cancion ->
            Text("${cancion.nombre} - ${cancion.artista} (${cancion.duracion})")
        }
    }
}


@Composable
fun PantallaAgregarCancion( /* navController: NavHostController, playlists: SnapshotStateList<Playlist> */ ){

    // Notese la primera letra mayuscula en cada una de las variables
    var Nombre by remember { mutableStateOf("")}
    var Artista by remember { mutableStateOf("")}
    var Duracion by remember { mutableStateOf("")}

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Agregar una cancion", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = Nombre,
            onValueChange = { Nombre = it },
            label = { Text("Título") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = Artista,
            onValueChange = { Artista = it },
            label = { Text("Artista") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = Duracion,
            onValueChange = { Duracion = it },
            label = { Text("Duración") }
        )

    }


}


//Función para crear las cards de la pestaña de playlists
@Composable
fun PlaylistCard(name: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name)
            Button(onClick = {

            }) {
                Text("Editar")
            }
        }
    }
}

//Funcion para generar la vista de las canciones en las playlists
@Composable
fun VistaCanciones(nombre: String, artista: String, duracion: String){

}


//Vistas previas
@Preview(showBackground = true)
@Composable
fun Preview() {

    //PantallaAgregarCancion()


    val navController = rememberNavController()
    val playlists = remember { mutableStateListOf<Playlist>() }
    App(navController, playlists)

}