package io.github.jonathan3467.music.Vista

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.jonathan3467.music.modelo.RolaDetallada

//muestra los datos de la rola seleccionada
@Composable
fun PamelLateral(seleccionada: RolaDetallada?, modifier: Modifier = Modifier){
    Column(modifier.padding(16.dp)) {
        if (seleccionada == null){
            Text(
                "Selecciona una rola",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Dato("Rola", seleccionada.rola.titulo)
            Dato("Interprete", seleccionada.performer)
            Dato("Album", seleccionada.album)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Dato("Pista", numeroOGuion(seleccionada.rola.pista))
            Dato("Año",  numeroOGuion(seleccionada.rola.year))
            Dato("Genero", seleccionada.rola.genero)
        }
    }
}

//una etiqueta chikitita arriba y el valor abajo
@Composable
private fun Dato(etiqueta: String, valor: String){
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)){
        Text(
            etiqueta,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            valor,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// el minero usa 0 cuando no sabe la pista o el año, se le pondra un guion para mejor vista
private fun numeroOGuion(numero: Int): String =
    if (numero == 0) "-" else numero.toString()