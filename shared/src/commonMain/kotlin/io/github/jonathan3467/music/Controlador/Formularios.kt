package io.github.jonathan3467.music.Controlador

import io.github.jonathan3467.music.modelo.TipoPerformer

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

data class FormularioPerformer(
    val nombre: String,
    val tipo: TipoPerformer,
    val nombreReal: String = "",
    val nacimiento: String = "",
    val muerte: String = "",
    val inicio: String = "",
    val fin: String= ""
){
    fun error(): String? {
        if (nombre.isBlank()) return "El nombre no puede quedar vacio"
        return when(tipo){
            TipoPerformer.PERSONA -> errorDeFechas(nacimiento, muerte, "nacimiento", "muerte")
            TipoPerformer.GRUPO -> errorDeFechas(inicio, fin, "inicio", "fin")
            TipoPerformer.DESCONOCIDO -> null
        }
    }
}

data class FormularioPersona(
    val nombre: String,
    val nombreReal: String = "",
    val nacimiento: String = "",
    val muerte: String = ""
){
    fun error(): String? =
        if (nombre.isBlank()) "El nombre no puede quedar vacio"
    else errorDeFechas(nacimiento,muerte, "nacimiento", "muerte")
}

//un numero entero que no sea negativo
private fun esNumeroValido(texto: String): Boolean =
    (texto.trim().toIntOrNull() ?: -1) >= 0

// revisa dos fechas opcionales, buen formato y que la segunda no sea anterior a la primera
private fun errorDeFechas(
    desde: String,
    hasta: String,
    nombreDesde: String,
    nombreHasta: String
): String? = when {
    desde.isNotBlank() && !esFechaValida(desde) ->
        "La fecha de $nombreDesde debe de ser una fecha real con formato AAAA-MM-DD"
    hasta.isNotBlank() && !esFechaValida(hasta) ->
        "La fecha de $nombreHasta debe de ser una fecha real con formato AAAA-MM-DD"
    desde.isNotBlank() && hasta.isNotBlank() && hasta.trim() < desde.trim() ->
        "La fecha de $nombreHasta no puede ser anterior a la de $nombreDesde"
    else -> null
}

//una fecha que existe, con formato AAAA-MM-DD por ejemplo 1960-01-01
fun esFechaValida(texto: String): Boolean {
    val partes = Regex("""(\d{4})-(\d{2})-(\d{2})""").matchEntire(texto.trim()) ?: return false
    val (a, m, d) = partes.destructured
    val year = a.toInt()
    val mes = m.toInt()
    val dia = d.toInt()
    return mes in 1..12 && dia in 1..diasDelMes(year,mes)
}

private fun diasDelMes(year: Int, mes: Int): Int = when(mes){
    2 -> if (esBisiesto(year)) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
}

private fun esBisiesto(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

// texto vacio se vuelve null (un dato desconocido) o si no el texto sin espacios de mas
fun String.oNulo(): String? = trim().ifEmpty { null}
