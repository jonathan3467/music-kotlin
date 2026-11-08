package io.github.jonathan3467.music.Compilador

// un error en lo que escribio el usuario, el mensaje se muestra en la ventana
class ErrorDeSintaxis(mensaje: String) : Exception(mensaje)

//Las piezas en las que se corta el texto de una busqueda
sealed interface Token{
    //donde empieza el token en el texto, contando desde 0
    val posicion: Int

    //una palabra suelta: un campo(titulo, año..), un valor (beatles) o un numero (1945)
    data class Palabra(val texto: String, override val posicion: Int) : Token

    //un texto entre comillas, que pueden tener espacios "no woman no cry"
    data class Cadena(val texto: String, override val posicion: Int) : Token

    // =, !=, <, <=, >, >=
    data class Comparador(val operador: String, override val posicion: Int) : Token

    data class DosPuntos(override val posicion: Int) : Token
    data class Y(override val posicion: Int) : Token
    data class O(override val posicion: Int) : Token
    data class No(override val posicion: Int) : Token
    data class AbreParentesis(override val posicion: Int) : Token
    data class CierraParentesis(override val posicion: Int) : Token
}