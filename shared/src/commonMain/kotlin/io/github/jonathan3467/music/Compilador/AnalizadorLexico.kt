package io.github.jonathan3467.music.Compilador

import kotlin.collections.plusAssign

//recorre el texto letra por letra y lo corta en tokens
class AnalizadorLexico(private val texto: String) {
    //la letra en la que vamos
    private var i = 0
    private val tokens = mutableListOf<Token>()

    fun analizar(): List<Token>{
        i = 0
        tokens.clear()

        while (i < texto.length){
            val c = texto[i]
            when{
                c.isWhitespace() -> i++
                c == '(' -> agregar(Token.AbreParentesis(i), largo = 1)
                c == ')' -> agregar(Token.CierraParentesis(i), largo = 1)
                c == ':' -> agregar(Token.DosPuntos(i), largo = 1)
                c == '"' -> leerCadena()
                c == '&' -> leerDoble('&') { Token.Y(it) }
                c == '|' -> leerDoble('|') { Token.O(it) }
                c == '!' ->
                    if (siguienteEs('=')) agregar(Token.Comparador("!=", i), largo = 2)
                    else agregar(Token.No(i), largo = 1)
                c == '<' || c == '>' ->
                    if (siguienteEs('=')) agregar(Token.Comparador("$c=", i), largo = 2)
                    else agregar(Token.Comparador("$c", i), largo = 1)
                c == '=' -> agregar(Token.Comparador("=", i), largo = 1)
                else -> leerPalabra()
            }
        }
        return tokens.toList()
    }

    private fun agregar(token: Token, largo: Int){
        tokens.add(token)
        i += largo
    }

    private fun siguienteEs(c: Char): Boolean =
        i + 1 < texto.length && texto[i + 1] == c

    // Para && y || el simbolo tiene que venir dos veces seguidas
    private fun leerDoble(simbolo: Char, crear: (Int) -> Token){
        if (!siguienteEs(simbolo)){
            throw ErrorDeSintaxis(
                "Se esperaba \"$simbolo$simbolo\" en la posicion ${i + 1}"
            )
        }
        agregar(crear(i), largo = 2)
    }

    //todo lo que hya entre comillas con espacios incluidos
    private fun leerCadena(){
        val inicio = i
        val cierre = texto.indexOf('"', startIndex = i + 1)
        if (cierre == -1){
            throw ErrorDeSintaxis(
                "Faltan las comillas de cierre (abren en la posicion ${inicio + 1})"
            )
        }
        tokens.add(Token.Cadena(texto.substring(inicio + 1, cierre), inicio))
        i = cierre + 1
    }

    // Lee hasta un espacio o un simbolo. Si la palabra es y, o, no, es un operador
    private fun leerPalabra(){
        val inicio = i
        while (i < texto.length && !terminaPalabra(texto[i])){
            i++
        }
        val palabra = texto.substring(inicio,i)
        tokens.add(
            when (palabra.lowercase()){
                "y" -> Token.Y(inicio)
                "o" -> Token.O(inicio)
                "no" -> Token.No(inicio)
                else -> Token.Palabra(palabra, inicio)
            }
        )
    }

    private fun terminaPalabra(c: Char): Boolean =
        c.isWhitespace() || c in "():\"<>=!&|"
}