package io.github.jonathan3467.music.Compilador

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AnalizadorLexicoTest {

    // Convierte los tokens en textos cortos para comparar facil
    private fun tokens(texto: String): List<String> =
        AnalizadorLexico(texto).analizar().map { t ->
            when(t) {
                is Token.Palabra -> "palabra(${t.texto})"
                is Token.Cadena -> "cadena(${t.texto})"
                is Token.Comparador -> t.operador
                is Token.DosPuntos -> ":"
                is Token.Y -> "Y"
                is Token.O -> "O"
                is Token.No -> "NO"
                is Token.AbreParentesis -> "("
                is Token.CierraParentesis -> ")"
            }
        }

    @Test
    fun cortaUnaCondicionSencilla(){
        assertEquals(
            listOf("palabra(interprete)", ":", "palabra(beatles)"),
            tokens("interprete: beatles")
        )
    }

    @Test
    fun reconoceYyOConSimbolosYConPalabras(){
        assertEquals(
            listOf("palabra(a)", "Y", "palabra(b)", "Y", "palabra(c)", "O", "palabra(d)", "O", "palabra(e)"),
            tokens("a && b y c || d O e")
        )
    }

    @Test
    fun reconoceLaNegacion(){
        assertEquals(
            listOf("NO", "palabra(a)", "NO", "palabra(b)"),
            tokens("!a no b")
        )
    }

    @Test
    fun reonoceLosComparadores(){
        assertEquals(
            listOf(
                "palabra(año)", ">=", "palabra(1985)",
                "Y", "palabra(pista)", "!=", "palabra(3)",
                "Y", "palabra(año)", "<", "palabra(2000)"
            ),
            tokens("año >= 1985 y pista != 3 y año<2000")
        )
    }

    @Test
    fun lasComillasJuntanVariasPalabras(){
        assertEquals(
            listOf("palabra(titulo)", ":", "cadena(no woman no cry)"),
            tokens("titulo: \"no woman no cry\"")
        )
    }

    @Test
    fun reconoceLosParentesis(){
        assertEquals(
            listOf("(", "palabra(a)", "O", "palabra(b)", ")"),
            tokens("(a o b)")
        )
    }

    @Test
    fun guardaLaPosicionDeCadaToken(){
        val resultado = AnalizadorLexico("  beatles").analizar()
        assertEquals(2, resultado.first().posicion)
    }

    @Test
    fun comillasSinCerrarEsError(){
        assertFailsWith<ErrorDeSintaxis> { AnalizadorLexico("titulo: \"sin cerrar").analizar() }

    }

    @Test
    fun unSoloAmpersandEsError(){
        assertFailsWith<ErrorDeSintaxis> { AnalizadorLexico("a & b").analizar() }
    }
}