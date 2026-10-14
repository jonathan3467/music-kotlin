package io.github.jonathan3467.music.modelo

//los tipos de interprete
enum class TipoPerformer (val id: Int){
    PERSONA(0),
    GRUPO(1),
    DESCONOCIDO(2);

    companion object{
        // convierte el id de la base al tipo, si no se reconoce regresa desconocido
        fun desdeId(id: Int): TipoPerformer =
            entries.firstOrNull {it.id == id} ?: DESCONOCIDO
    }
}