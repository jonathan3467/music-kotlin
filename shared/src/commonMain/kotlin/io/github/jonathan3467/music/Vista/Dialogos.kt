package io.github.jonathan3467.music.Vista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jonathan3467.music.Controlador.Dialogo
import io.github.jonathan3467.music.Controlador.FormularioAlbum
import io.github.jonathan3467.music.Controlador.FormularioRola

@Composable
fun DialogoEditarRola(
    dialogo: Dialogo.EditarRola,
    alGuardar: (FormularioRola) -> Unit,
    alCancelar: () -> Unit
){
    val d = dialogo.detallada
    // lo que el usuario va escribiendo, empieza con los datos actuales de la rola
    var titulo by remember(d) { mutableStateOf(d.rola.titulo) }
    var interprete by remember(d) { mutableStateOf(d.performer) }
    var pista by remember(d) { mutableStateOf(d.rola.pista.toString()) }
    var year by remember(d) { mutableStateOf(d.rola.year.toString()) }
    var genero by remember(d) { mutableStateOf(d.rola.genero) }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Editar rola") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                Campo("Titulo", titulo) { titulo = it }
                Campo("Interprete", interprete) { interprete = it }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    Campo("Pista", pista, Modifier.weight(1f)) { pista = it }
                    Campo("Año", year, Modifier.weight(1f)) { year = it }
                }
                Campo("Genero", genero) { genero = it }
                MensajeDeError(dialogo.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                alGuardar(FormularioRola(titulo, interprete, pista, year, genero))
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) {Text("Cancelar")}
        }
    )
}

@Composable
fun DialogoEditarAlbum(
    dialogo: Dialogo.EditarAlbum,
    alGuardar: (FormularioAlbum) -> Unit,
    alCancelar: () -> Unit
){
    val a = dialogo.album
    var nombre by remember(a) { mutableStateOf(a.nombre) }
    var year by remember(a) { mutableStateOf(a.year.toString()) }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Editar album") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Campo("Nombre", nombre) { nombre = it }
                Campo("Año", year) { year = it }
                Campo("Carpeta", a.ruta, soloLectura = true)
                MensajeDeError(dialogo.error)
            }
        },
        confirmButton = {
            Button(onClick = { alGuardar(FormularioAlbum(nombre, year))
            }) { Text("Guardar")}
        },
        dismissButton = {
            TextButton(onClick = alCancelar) { Text("Cancelar")}
        }
    )
}

@Composable
private fun Campo(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    soloLectura: Boolean = false,
    alCambiar: (String) -> Unit = {}
){
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        singleLine = true,
        readOnly = soloLectura,
        modifier = modifier
    )
}

@Composable
private fun MensajeDeError(error: String?){
    if (error != null){
        Text(error, color = MaterialTheme.colorScheme.error)
    }
}