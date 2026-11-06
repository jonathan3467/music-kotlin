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
import io.github.jonathan3467.music.Controlador.FormularioPerformer
import io.github.jonathan3467.music.Controlador.FormularioRola
import io.github.jonathan3467.music.modelo.TipoPerformer

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
fun DialogoEditarPerformer(
    dialogo: Dialogo.EditarPerformer,
    alGuardar: (FormularioPerformer) -> Unit,
    alCancelar: () -> Unit
){
    val p = dialogo.performer
    var nombre by remember(p) { mutableStateOf(p.nombre)}
    var tipo by remember(p) {mutableStateOf(p.tipo)}
    var nombreReal by remember(p) {mutableStateOf(dialogo.persona?.nombreReal ?: "")}
    var nacimiento by remember(p) {mutableStateOf(dialogo.persona?.fechaNacimiento ?: "")}
    var muerte by remember(p) {mutableStateOf(dialogo.persona?.fechaMuerte ?: "")}
    var inicio by remember(p) {mutableStateOf(dialogo.grupo?.fechaInicio ?: "")}
    var fin by remember(p) {mutableStateOf(dialogo.grupo?.fechaFin ?: "")}

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Editar interprete")},
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                Campo("Nombre", nombre) { nombre = it}

                // un boton por tipo: el escogido se ve marcado
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    TipoPerformer.entries.forEach { opcion ->
                        FilterChip(
                            selected = tipo == opcion,
                            onClick = { tipo = opcion },
                            label = { Text(etiquetaDe(opcion))}
                        )
                    }
                }

                //Los campos cambian segun el tipo escogido
                when(tipo){
                    TipoPerformer.PERSONA -> {
                        Campo("Nombre real", nombreReal) { nombreReal = it }
                        Campo("Nacimiento (AAAA-MM-DD)", nacimiento) { nacimiento = it }
                        Campo("Muerte (AAAA-MM-DD)", muerte) { muerte = it }
                    }
                    TipoPerformer.GRUPO -> {
                        Campo("Inicio (AAAA-MM-DD)", inicio) { inicio = it }
                        Campo("Fin (AAAA-MM-DD)", fin) { fin = it }
                    }
                    TipoPerformer.DESCONOCIDO -> {}
                }

                MensajeDeError(dialogo.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                alGuardar(FormularioPerformer(nombre, tipo, nombreReal, nacimiento, muerte, inicio, fin))
            }) { Text("Guardar")}
        },
        dismissButton = {
            TextButton(onClick = alCancelar) { Text("Cancelar") }
        }
    )
}

private fun etiquetaDe(tipo: TipoPerformer): String = when(tipo){
    TipoPerformer.PERSONA -> "Persona"
    TipoPerformer.GRUPO -> "Grupo"
    TipoPerformer.DESCONOCIDO -> "Desconocido"
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