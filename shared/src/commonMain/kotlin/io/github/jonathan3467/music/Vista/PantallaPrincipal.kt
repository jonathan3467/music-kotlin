package io.github.jonathan3467.music.Vista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.jonathan3467.music.Controlador.EstadoPantalla

// que tan ancha es cada collumna: titulo, interprete,album,genero
private val PESOS = listOf(3f, 2f, 3f, 1.5f)

//la vista solo dibuja el estado y avisa cuando aprietan el boton
@Composable
fun PantallaPrincipal(estado: EstadoPantalla, alMinar: () -> Unit){
    MaterialTheme(colorScheme = darkColorScheme()){
        Surface(Modifier.fillMaxSize()) {
            Column(androidx.compose.ui.Modifier.fillMaxSize().padding(16.dp)){

                Row(verticalAlignment = Alignment.CenterVertically){
                    Button(onClick = alMinar, enabled = !estado.minando){
                        Text("Minar carpeta...")
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(estado.mensaje ?: "${estado.rolas.size} rolas")
                }

                Spacer(Modifier.height(16.dp))
                Fila(listOf("Titutlo","Interprete", "Album", "Genero"), encabezado = true)
                HorizontalDivider()

                LazyColumn(Modifier.fillMaxSize()){
                    items(estado.rolas, key = {it.rola.id}) {detallada ->
                        Fila(
                            listOf(
                                detallada.rola.titulo,
                                detallada.performer,
                                detallada.album,
                                detallada.rola.genero
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Fila(textos: List<String>, encabezado: Boolean = false){
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)){
        textos.forEachIndexed { i, texto ->
            Text(
                text = texto,
                modifier = Modifier.weight(PESOS[i]).padding(end = 8.dp),
                fontWeight = if (encabezado) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}