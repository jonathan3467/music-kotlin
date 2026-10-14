package io.github.jonathan3467.music.modelo

data class Rola(
    val id: Long,
    val idPerformer: Long,
    val idAlbum: Long,
    val ruta: String,
    val titulo: String,
    val pista: Int,
    val year: Int,
    val genero: String
)
