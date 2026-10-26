package io.github.jonathan3467.music.controlador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.jonathan3467.music.Controlador.EstadoPantalla
import io.github.jonathan3467.music.basedatos.RepositorioMusica
import io.github.jonathan3467.music.minero.Importador
import io.github.jonathan3467.music.minero.Minero
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// va a recibir las acciones de la vista, le va a pedir las cosas al modelo y actualiza vista
class ControladorPrincipal (
    private val repo: RepositorioMusica,
    private val alcance: CoroutineScope
){
    private val minero = Minero()
    private val importador = Importador(repo)

    //cuando cambia la vista se vuelve a dibujar
    var estado by mutableStateOf(EstadoPantalla())
        private set

    //lee de la base lo que ya estaba guardado (al abrir el programa)
    fun cargarRolas(){
        estado = estado.copy(rolas = repo.todasLasRolasDetalladas())
    }

    fun minar(directorio: File){
        if (estado.minando) return
        estado = estado.copy(minando = true, mensaje = "Minando ${directorio.name}...")
        alcance.launch {
            // se hace el trabajo en otro hilo para que no se congele
            val resultado = withContext(Dispatchers.IO){
                runCatching {
                    val cuantas = importador.importar(minero.minar(directorio))
                    cuantas to repo.todasLasRolasDetalladas()
                }
            }
            estado = resultado.fold(
                onSuccess = { (cuantas, rolas) ->
                    EstadoPantalla(rolas = rolas, mensaje = "Se minaron $cuantas rolas")
                },
                onFailure = {
                    error ->
                    estado.copy(minando = false, mensaje = "Error al minar: ${error.message}")
                }
            )
        }
    }
}