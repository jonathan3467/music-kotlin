package io.github.jonathan3467.music.Compilador

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class GeneradorSqlTest {

    // lo que se escribe em el SQL para "contiene"
    private val LIKE = "LIKE ? ESCAPE '\\'"

    @Test
    fun condicionDeTexto(){
        assertEquals(
            ConsultaSql("p.name $LIKE", listOf("%beatles%")),
            compilarBusqueda("interprete: beatles")
        )
    }

    @Test
    fun comparacionDeNumero(){
        assertEquals(
            ConsultaSql("r.year > ?", listOf(1965)),
            compilarBusqueda("año > 1965")
        )
    }

    @Test
    fun yOyNoConParametrosEnOrden(){
        assertEquals(
            ConsultaSql(
                "((r.genre $LIKE OR r.genre $LIKE) AND NOT (r.year < ?))",
                listOf("%rock%", "%pop%", 2000)
            ),
            compilarBusqueda("(genero: rock o genero: pop) y no año < 2000")
        )
    }

    @Test
    fun palabraSuletaBuscaEnTresColumnas(){
        assertEquals(
            ConsultaSql(
                "(r.title $LIKE OR p.name $LIKE OR a.name $LIKE)",
                listOf("%help%", "%help%", "%help%")
            ),
            compilarBusqueda("help")
        )
    }

    @Test
    fun escapaLosComodinesDeLike(){
        assertEquals(listOf("%100\\%%"), compilarBusqueda("titulo: 100%").parametros)
    }

    @Test
    fun elTextoDelUsuarioNuncaQuedaDentroDelSql(){
        val consulta = compilarBusqueda("titulo: \"x' OR '1'='1\"")
        assertFalse(consulta.where.contains("OR '1'"))
    }
}