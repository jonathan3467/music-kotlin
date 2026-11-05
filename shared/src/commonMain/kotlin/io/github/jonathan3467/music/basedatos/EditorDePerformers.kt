package io.github.jonathan3467.music.basedatos

import io.github.jonathan3467.music.modelo.Grupo
import io.github.jonathan3467.music.modelo.Performer
import io.github.jonathan3467.music.modelo.Persona
import io.github.jonathan3467.music.modelo.TipoPerformer


//los datos extra de una persona (su nombre artistico es el nombre del performer)
data class DatosPersona(
    val nombreReal: String? = null,
    val nacimiento: String? = null,
    val muerte: String? =  null
)

// los datos extra de un grupo (su nombre es el nombre del performer)
data class DatosGrupo(
    val inicio: String? = null,
    val fin: String? = null
)

// se lanza si el nombre nuevo ya lo tiene otro interprete
class NombreRepetidoException(nombre: String):
        Exception("Ya existe un interprete llamado \"$nombre\"")

// guarda los cambios de un interprete y mantiene el enlace por nombre
// con su persona o grupo
class EditorDePerformers(private val repo: RepositorioMusica) {

    fun guardar(
        idPerformer: Long,
        nombre: String,
        tipo: TipoPerformer,
        datosPersona: DatosPersona = DatosPersona(),
        datosGrupo: DatosGrupo = DatosGrupo()
    ){
        repo.enTransaccion {
            val anterior = repo.buscarPerformerPorId(idPerformer)
                ?: throw IllegalArgumentException("No existe el interprete $idPerformer")

            val otro = repo.buscarPerformerPorNombre(nombre)
            if (otro != null && otro.id != idPerformer){
                throw NombreRepetidoException(nombre)
            }
            //si cambio de tipo, los datos del tipo anterior ya no sirven
            if (anterior.tipo != tipo){
                borrarDetalles(anterior)
            }

            repo.actualizarPerformer(anterior.copy(nombre = nombre, tipo = tipo))
            when(tipo){
                TipoPerformer.PERSONA -> guardarPersona(anterior.nombre, nombre, datosPersona)
                TipoPerformer.GRUPO -> guardarGrupo(anterior.nombre, nombre, datosGrupo)
                TipoPerformer.DESCONOCIDO -> {}
            }
        }
    }

    private fun borrarDetalles(performer: Performer){
        when(performer.tipo){
            TipoPerformer.PERSONA ->
                repo.buscarPersona(performer.nombre)?.let { repo.borrarPersona(it.id) }
            TipoPerformer.GRUPO ->
                repo.buscarGrupo(performer.nombre)?.let { repo.borrarGrupo(it.id) }
            TipoPerformer.DESCONOCIDO -> {}
        }
    }

    //Busca la persona por el nombre anterior (por si el performer se renombro)
    //o por el nuevo, si existe la actualiza si no la crea
    private fun guardarPersona(nombreAnterior: String, nombre: String, datos: DatosPersona){
        val existente = repo.buscarPersona(nombreAnterior) ?: repo.buscarPersona(nombre)
        val persona = Persona(
            id = existente?.id ?: 0,
            nombreArtistico = nombre,
            nombreReal = datos.nombreReal,
            fechaNacimiento = datos.nacimiento,
            fechaMuerte = datos.muerte
        )
        if (existente == null) repo.insertarPersona(persona)
        else repo.actualizarPersona(persona)
    }

    private fun guardarGrupo(nombreAnterior: String, nombre: String, datos: DatosGrupo){
        val existente = repo.buscarGrupo(nombreAnterior) ?: repo.buscarGrupo(nombre)
        val grupo = Grupo(
            id = existente?.id ?: 0,
            nombre = nombre,
            fechaInicio = datos.inicio,
            fechaFin = datos.fin
        )
        if (existente == null) repo.insertarGrupo(grupo)
        else repo.actualizarGrupo(grupo)
    }
}