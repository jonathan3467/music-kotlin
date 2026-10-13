package io.github.jonathan3467.music.modelo

// la informacion que el minero saca de un archivo MP3
// antes de guardarla en la base de datos
data class RolaMinada(
    val ruta: String,         // ruta completa del archivo MP3
    val rutaAlbum: String,    // carpeta donde esta el MP3
    val titulo: String,
    val interprete: String,
    val album: String?,  //null = sin album; luego ira al algum uknown
    val year: Int,
    val pista: Int,
    val genero: String
)
