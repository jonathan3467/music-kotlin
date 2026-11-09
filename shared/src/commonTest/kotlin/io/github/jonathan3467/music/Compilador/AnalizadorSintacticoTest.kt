package io.github.jonathan3467.music.Compilador
import io.github.jonathan3467.music.Compilador.Expresion.Compara
import io.github.jonathan3467.music.Compilador.Expresion.Contiene
import io.github.jonathan3467.music.Compilador.Expresion.EnTodo
import io.github.jonathan3467.music.Compilador.Expresion.No
import io.github.jonathan3467.music.Compilador.Expresion.O
import io.github.jonathan3467.music.Compilador.Expresion.Y
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AnalizadorSintacticoTest {

    @Test
    fun condicionDeTexto(){
        assertEquals(
            Contiene(CampoTexto.INTERPRETE, "beatles"),
            analizarBusqueda("interprete: beatles")
        )
    }

    @Test
    fun aceptaCamposConAcentosYMayusculas(){
        assertEquals(Contiene(CampoTexto.GENERO, "rock"), analizarBusqueda("Género: rock"))
    }

    @Test
    fun comparacionDeNumeros(){
        assertEquals(
            Compara(CampoNumero.YEAR, Operador.MAYOR_IGUAL, 1965),
            analizarBusqueda("año >= 1965")
        )
    }

    @Test
    fun dosPuntosEnUnNumeroEsIgual(){
        assertEquals(Compara(CampoNumero.PISTA, Operador.IGUAL, 3), analizarBusqueda("pista: 3"))
    }

    @Test
    fun palabraSueltaBuscaEnTodo(){
        assertEquals(EnTodo("beatles"), analizarBusqueda("beatles"))
    }

    @Test
    fun lasComillasSonUnSoloTexto(){
        assertEquals(
            Contiene(CampoTexto.TITULO, "no woman no cry"),
            analizarBusqueda("titulo: \"no woman no cry\"")
        )
    }

    @Test
    fun yVaAntesQueO(){
        //a o (b y c)
        assertEquals(
            O(EnTodo("a"), Y(EnTodo("b"), EnTodo("c"))),
            analizarBusqueda("a o b y c")
        )
    }

    @Test
    fun losParentesisCambianElOrden(){
        assertEquals(
            Y(O(EnTodo("a"), EnTodo("b")), EnTodo("c")),
            analizarBusqueda("(a o b) y c")
        )
    }

    @Test
    fun noVaAntesQueY(){
        // (no a) y b
        assertEquals(
            Y(No(EnTodo("a")), EnTodo("b")),
            analizarBusqueda("no a y b")
        )
    }

    @Test
    fun palabrasSeguidasSeJuntanConY(){
        assertEquals(
            Y(Y(EnTodo("let"), EnTodo("it")), EnTodo("be")),
            analizarBusqueda("let it be")
        )
    }

    @Test
    fun unaBusquedaCompleta(){
        assertEquals(
            Y(
                O(Contiene(CampoTexto.GENERO, "rock"), Contiene(CampoTexto.GENERO, "pop")),
                Compara(CampoNumero.YEAR, Operador.MAYOR, 2000)
            ),
            analizarBusqueda("(genero: rock || genero: pop) && año > 2000")
        )
    }

    @Test
    fun erroresDeEscritura(){
        val malas = listOf(
            "",         //vacia
            "año > rock",       // se esperaba un numero
            "año >",            // falta un numero
            "color: rojo",      // no existe el campo
            "titulo > a",       // un campo de texto no se compara
            "(a o b",           // falta cerrar el parentesis
            "a o b)",           // falta abrir el parentesis
            "y beatles",        //empieza con un operador
            "interprete:"       // falta el texto
        )
        for (busqueda in malas){
            assertFailsWith<ErrorDeSintaxis>("Deberia fallar: $busqueda"){
                analizarBusqueda(busqueda)
            }
        }
    }
}