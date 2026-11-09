package io.github.jonathan3467.music.Compilador

import music_kotlin.shared.generated.resources.Res

// el arbol que resulta de analizar una busqueda cada nodo es una de estas opciones
sealed interface Expresion{

    data class Y(val izquierdo: Expresion, val derecha: Expresion) : Expresion

    data class O(val izquierdo: Expresion, val derecha: Expresion) : Expresion

    data class No(val expresion: Expresion) : Expresion

    //campo: texto -> el campo contiene ese texto como ejemplo interprete: beatles
    data class Contiene(val campo: CampoTexto, val texto: String) : Expresion

    //campo comparador numero ejemplo año > 1965
    data class Compara(val campo: CampoNumero, val operador: Operador, val numero: Int) : Expresion

    //una palabra sin campo se busca en el titulo, interprete y el album
    data class EnTodo(val texto: String) : Expresion
}

enum class CampoTexto { TITULO, INTERPRETE, ALBUM, GENERO }

enum class CampoNumero { YEAR, PISTA }

enum class Operador(val simbolo: String){
    IGUAL("="),
    DISTINTO("!="),
    MENOR("<"),
    MENOR_IGUAL("<="),
    MAYOR(">"),
    MAYOR_IGUAL(">=")
}