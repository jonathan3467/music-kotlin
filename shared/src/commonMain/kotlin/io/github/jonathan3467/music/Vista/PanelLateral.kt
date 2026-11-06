package io.github.jonathan3467.music.Vista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.jonathan3467.music.modelo.RolaDetallada

//muestra los datos de la rola seleccionada
@Composable
fun PanelLateral(seleccionada: RolaDetallada?,
                 alEditarRola: () -> Unit,
                 alEditarAlbum: () -> Unit,
                 alEditarPerformer: () -> Unit,
                 modifier: Modifier = Modifier
){
    Column(modifier.padding(16.dp)) {
        if (seleccionada == null){
            Text(
                "Selecciona una rola",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Dato("Rola", seleccionada.rola.titulo,alEditarRola)
            Dato("Interprete", seleccionada.performer, alEditarPerformer)
            Dato("Album", seleccionada.album, alEditarAlbum)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Dato("Pista", numeroOGuion(seleccionada.rola.pista))
            Dato("Año",  numeroOGuion(seleccionada.rola.year))
            Dato("Genero", seleccionada.rola.genero)
        }
    }
}

//una etiqueta chikitita arriba y el valor abajo, si tiene accion se puede hacer click
@Composable
private fun Dato(etiqueta: String, valor: String, alHacerClic: (() -> Unit)? = null){
    val clic = if (alHacerClic != null) Modifier.clickable(onClick = alHacerClic) else Modifier
    Row(
        Modifier.fillMaxWidth().then(clic).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
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
        if (alHacerClic != null) {
            Text(
                "Editar",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// el minero usa 0 cuando no sabe la pista o el año, se le pondra un guion para mejor vista
private fun numeroOGuion(numero: Int): String =
    if (numero == 0) "-" else numero.toString()