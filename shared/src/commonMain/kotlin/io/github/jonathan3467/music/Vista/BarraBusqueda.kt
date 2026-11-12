package io.github.jonathan3467.music.Vista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp

private const val AYUDA =
    "Campos: titulo, interprete, album, genero, año, pista. " +
            "Operadores: y, o, no, parentesis. Textos con espacios entre comillas"

@Composable
fun BarraDeBusqueda(
    busquedaActual: String,
    error: String?,
    alBuscar: (String) -> Unit,
    alLimpiar: () -> Unit,
    modifier: Modifier = Modifier
){
    //Lo que el usuario va escribiendo. Si la busqueda activa cambia
    //desde afuera (por ejemplo, al minar) , el texto se reinicia
    var texto by remember(busquedaActual) { mutableStateOf(busquedaActual) }

    Column(modifier){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                placeholder = { Text("Buscar... por ejemplo: interprete: malilla y año > 2000") },
                singleLine = true,
                isError = error != null,
                modifier = Modifier.weight(1f)
                //enter tambien busca no solo el boton
                    .onPreviewKeyEvent { evento ->
                        if (evento.key == Key.Enter && evento.type == KeyEventType.KeyDown){
                            alBuscar(texto)
                            true
                        } else {
                            false
                        }
                    }
            )
            Button(onClick = { alBuscar(texto)}) { Text("Buscar")}
            if (busquedaActual.isNotEmpty() || texto.isNotEmpty()){
                TextButton(onClick = {
                    texto = ""
                    alLimpiar()
                }) { Text("Limpiar") }
            }
        }

        //debajo el error en rojo, o una ayuda chiquita si no hay error
        Text(
            text = error ?: AYUDA,
            style = MaterialTheme.typography.labelSmall,
            color = if (error != null) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )
    }
}