package io.github.jonathan3467.music.Vista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jonathan3467.music.Controlador.Dialogo
import io.github.jonathan3467.music.Controlador.FormularioAlbum
import io.github.jonathan3467.music.Controlador.FormularioPerformer
import io.github.jonathan3467.music.Controlador.FormularioPersona
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
    alVerMiembros: () -> Unit,
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
                        if (dialogo.grupo != null && p.tipo == TipoPerformer.GRUPO){
                            OutlinedButton(onClick = alVerMiembros) { Text("Miembros...")}
                        } else {
                            Text(
                                "Guardar primero como grupo para poder agregar miembros",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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

@Composable
fun DialogoMiembros(
    dialogo: Dialogo.Miembros,
    alAgregar: (String) -> Unit,
    alQuitar: (Long) -> Unit,
    alCerrar: () -> Unit
){
    var nombre by remember(dialogo.grupo) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Miembros de ${dialogo.grupo.nombre}")},
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                if (dialogo.miembros.isEmpty()){
                    Text("Este grupo todavia no tiene miembros",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                dialogo.miembros.forEach { persona ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                        Text(persona.nombreArtistico, Modifier.weight(1f))
                        TextButton(onClick = { alQuitar(persona.id) }) { Text("Quitar") }
                    }
                }

                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Campo("Nombre de la persona", nombre, Modifier.weight(1f)){ nombre = it }
                    Button(onClick = {
                        alAgregar(nombre)
                        nombre = ""
                    }) { Text("Agregar") }
                }
                MensajeDeError(dialogo.error)
            }
        },
        confirmButton = {
            Button(onClick = alCerrar) { Text("Listo") }
        }
    )
}

@Composable
fun DialogoNuevaPersona(
    dialogo: Dialogo.NuevaPersona,
    alGuardar: (FormularioPersona) -> Unit,
    alCancelar: () -> Unit
){
    var nombre by remember { mutableStateOf("") }
    var nombreReal by remember { mutableStateOf("") }
    var nacimiento by remember { mutableStateOf("") }
    var muerte by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = alCancelar,
        title = { Text("Nueva Persona") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                Campo("Nombre artistico", nombre) { nombre = it }
                Campo("Nombre real", nombreReal) { nombreReal = it }
                Campo("Nacimiento (AAAA-MM-DD)", nacimiento) {nacimiento = it }
                Campo("Muerte (AAAA-MM-DD)", muerte) { muerte = it }
                MensajeDeError(dialogo.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                alGuardar(FormularioPersona(nombre, nombreReal, nacimiento, muerte))
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