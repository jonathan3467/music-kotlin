package io.github.jonathan3467.music.Controlador

// lo que el usuario escribio en el dialogo de rola
data class FormularioRola(
    val titulo: String,
    val interprete: String,
    val pista: String,
    val year: String,
    val genero: String
){
    //regresa el mensaje de error, o null si todo esta bien
    fun error(): String? = when {
        titulo.isBlank() -> "El titulo no puede quedar vacio"
        interprete.isBlank() -> "El interprete no puede quedar vacio"
        !esNumeroValido(pista) -> "La pista debe de ser un numero (0 si no se sabe)"
        !esNumeroValido(year) -> "El año debe de ser un numero (0 si no se sabe)"
        else -> null
    }
}

data class FormularioAlbum(
    val nombre: String,
    val year: String
){
    fun error(): String? = when {
        nombre.isBlank() -> "El nombre no puede quedar vacio"
        !esNumeroValido(year) -> "El año debe de ser un numero (0 si no se sabe)"
        else -> null
    }
}

//un numero entero que no sea negativo
private fun esNumeroValido(texto: String): Boolean =
    (texto.trim().toIntOrNull() ?: -1) >= 0