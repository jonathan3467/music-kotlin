package io.github.jonathan3467.music.modelo

data class Album(
    val id: Long,
    val ruta: String,
    val nombre: String,
    val year: Int
){
    companion object{
        //el album unknown
        const val ID_DESCONOCIDO = 0L
    }
}
