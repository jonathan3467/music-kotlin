package io.github.jonathan3467.music.controlador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.jonathan3467.music.Compilador.ErrorDeSintaxis
import io.github.jonathan3467.music.Compilador.compilarBusqueda
import io.github.jonathan3467.music.Controlador.AccionesPantalla
import io.github.jonathan3467.music.Controlador.Dialogo
import io.github.jonathan3467.music.Controlador.EstadoPantalla
import io.github.jonathan3467.music.Controlador.FormularioAlbum
import io.github.jonathan3467.music.Controlador.FormularioPerformer
import io.github.jonathan3467.music.Controlador.FormularioPersona
import io.github.jonathan3467.music.Controlador.FormularioRola
import io.github.jonathan3467.music.Controlador.oNulo
import io.github.jonathan3467.music.basedatos.DatosGrupo
import io.github.jonathan3467.music.basedatos.DatosPersona
import io.github.jonathan3467.music.basedatos.EditorDePerformers
import io.github.jonathan3467.music.basedatos.NombreRepetidoException
import io.github.jonathan3467.music.basedatos.RepositorioMusica
import io.github.jonathan3467.music.minero.Importador
import io.github.jonathan3467.music.minero.Minero
import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.Persona
import io.github.jonathan3467.music.modelo.RolaDetallada
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// va a recibir las acciones de la vista, le va a pedir las cosas al modelo y actualiza vista
class ControladorPrincipal (
    private val repo: RepositorioMusica,
    private val alcance: CoroutineScope
) : AccionesPantalla {
    private val minero = Minero()
    private val importador = Importador(repo)
    private val editor = EditorDePerformers(repo)

    //cuando cambia la vista se vuelve a dibujar
    var estado by mutableStateOf(EstadoPantalla())
        private set

    //lee de la base lo que ya estaba guardado (al abrir el programa)
    fun cargarRolas(){
        estado = estado.copy(rolas = repo.todasLasRolasDetalladas())
    }

    override fun seleccionar(idRola: Long){
        estado = estado.copy(idSeleccionada = idRola)
    }

    override fun editarRolaSeleccionada() {
        val seleccionada = estado.seleccionada ?: return
        estado = estado.copy(dialogo = Dialogo.EditarRola(seleccionada))
    }

    override fun editarAlbumSeleccionado() {
        val seleccionada = estado.seleccionada ?: return
        if (seleccionada.rola.idAlbum == Album.ID_DESCONOCIDO){
            estado = estado.copy(mensaje = "Esta rola no tiene album")
            return
        }
        val album = repo.buscarAlbumPorId(seleccionada.rola.idAlbum) ?: return
        estado = estado.copy(dialogo = Dialogo.EditarAlbum(album))
    }

    override fun cerrarDialogo() {
        estado = estado.copy(dialogo = null)
    }

    override fun guardarRola(formulario: FormularioRola) {
        val dialogo = estado.dialogo as? Dialogo.EditarRola ?: return
        val error = formulario.error()
        if (error != null){
            estado = estado.copy(dialogo = dialogo.copy(error = error))
            return
        }

        repo.enTransaccion {
            val performer = repo.obtenerOCrearPerformer(formulario.interprete.trim())
            repo.actualizarRola(
                dialogo.detallada.rola.copy(
                    idPerformer = performer.id,
                    titulo = formulario.titulo.trim(),
                    pista = formulario.pista.trim().toInt(),
                    year = formulario.year.trim().toInt(),
                    genero = formulario.genero.trim()
                )
            )
        }
        recargar()
    }

    override fun guardarAlbum(formulario: FormularioAlbum) {
        val dialogo = estado.dialogo as? Dialogo.EditarAlbum ?: return
        val error = formulario.error()
        if (error != null){
            estado = estado.copy(dialogo = dialogo.copy(error = error))
            return
        }

        repo.actualizarAlbum(
            dialogo.album.copy(
                nombre = formulario.nombre.trim(),
                year = formulario.year.trim().toInt()
            )
        )
        recargar()
    }

    override fun editarPerformerSeleccionado() {
        val seleccionado = estado.seleccionada ?: return
        val performer = repo.buscarPerformerPorId(seleccionado.rola.idPerformer) ?: return
        estado = estado.copy(
            dialogo = Dialogo.EditarPerformer(
                performer = performer,
                persona = repo.buscarPersona(performer.nombre),
                grupo = repo.buscarGrupo(performer.nombre)
            )
        )
    }

    override fun guardarPerformer(formulario: FormularioPerformer) {
        val dialogo = estado.dialogo as? Dialogo.EditarPerformer ?: return
        val error = formulario.error()
        if (error != null){
            estado = estado.copy(dialogo = dialogo.copy(error = error))
            return
        }

        try {
            editor.guardar(
                idPerformer = dialogo.performer.id,
                nombre = formulario.nombre.trim(),
                tipo = formulario.tipo,
                datosPersona = DatosPersona(
                    nombreReal = formulario.nombreReal.oNulo(),
                    nacimiento = formulario.nacimiento.oNulo(),
                    muerte = formulario.muerte.oNulo()
                ),
                datosGrupo = DatosGrupo(
                    inicio = formulario.inicio.oNulo(),
                    fin = formulario.fin.oNulo()
                )
            )
        } catch (e: NombreRepetidoException){
            estado = estado.copy(dialogo = dialogo.copy(error = e.message))
            return
        }
        recargar()
    }

    override fun verMiembros() {
        val dialogo = estado.dialogo as? Dialogo.EditarPerformer ?: return
        val grupo = dialogo.grupo ?: return
        estado = estado.copy(dialogo = Dialogo.Miembros(grupo, repo.miembrosDe(grupo.id)))
    }

    override fun agregarMiembro(nombre: String) {
        val dialogo = estado.dialogo as? Dialogo.Miembros ?: return
        val limpio = nombre.trim()
        if (limpio.isEmpty()){
            estado = estado.copy(dialogo = dialogo.copy(error = "Escribe el nombre de la persona"))
            return
        }

        repo.enTransaccion {
            val persona = repo.obtenerOCrearPersona(limpio)
            repo.agregarMiembro(persona.id, dialogo.grupo.id)
        }
        actualizarMiembros(dialogo)
    }

    override fun quitarMiembro(idPersona: Long) {
        val dialogo = estado.dialogo as? Dialogo.Miembros ?: return
        repo.quitarMiembro(idPersona, dialogo.grupo.id)
        actualizarMiembros(dialogo)
    }

    override fun nuevaPersona() {
        estado = estado.copy(dialogo = Dialogo.NuevaPersona())
    }

    override fun guardarNuevaPersona(formulario: FormularioPersona) {
        val dialogo = estado.dialogo as?  Dialogo.NuevaPersona ?: return
        val nombre = formulario.nombre.trim()
        val error = formulario.error()
            ?: if (repo.buscarPersona(nombre) != null) "Ya existe una persona llamada \"$nombre\"" else null
        if (error != null){
            estado = estado.copy(dialogo = dialogo.copy(error = error))
            return
        }

        repo.insertarPersona(
            Persona(
                id = 0,
                nombreArtistico = nombre,
                nombreReal = formulario.nombreReal.oNulo(),
                fechaNacimiento = formulario.nacimiento.oNulo(),
                fechaMuerte = formulario.muerte.oNulo()
            )
        )
        estado = estado.copy(dialogo = null, mensaje = "Se agrego a $nombre")
    }

    override fun buscar(texto: String) {
        if (texto.isBlank()){
            limpiarBusqueda()
            return
        }
        val consulta = try {
            compilarBusqueda(texto)
        } catch (e: ErrorDeSintaxis){
            // la lista se queda como estaba, solo se muestra el error
            estado = estado.copy(errorBusqueda = e.message)
            return
        }

        val rolas = repo.buscarRolas(consulta)
        estado = estado.copy(
            rolas = rolas,
            busqueda = texto,
            errorBusqueda = null,
            mensaje = if (rolas.size == 1) "1 rola encontrada" else "${rolas.size} rolas encontradas"
        )
    }

    override fun limpiarBusqueda() {
        estado = estado.copy(
            rolas = repo.todasLasRolasDetalladas(),
            busqueda = "",
            errorBusqueda = null,
            mensaje = null
        )
    }

    // las rolas que se deben ver, todas o solo las de la busqueda activa
    private fun rolasVisibles(): List<RolaDetallada> =
        if (estado.busqueda.isBlank()) repo.todasLasRolasDetalladas()
        else repo.buscarRolas(compilarBusqueda(estado.busqueda))

    //vuelve a leer los miembros para que el dialogo muestre la lista nueva
    private fun actualizarMiembros(dialogo: Dialogo.Miembros){
        estado = estado.copy(
            dialogo = dialogo.copy(miembros = repo.miembrosDe(dialogo.grupo.id), error = null)
        )
    }

    // vuelve a leer las rolas y cierra el dialogo
    private fun recargar(){
        estado = estado.copy(rolas = rolasVisibles(), dialogo = null)
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