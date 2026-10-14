package io.github.jonathan3467.music.modelo

data class Persona(
    val id: Long,
    val nombreArtistico: String,
    val nombreReal: String?,
    val fechaNacimiento: String?,
    val fechaMuerte: String?
)
