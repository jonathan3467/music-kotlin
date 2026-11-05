package io.github.jonathan3467.music.minero

import io.github.jonathan3467.music.basedatos.BaseDeDatos
import io.github.jonathan3467.music.basedatos.DatosGrupo
import io.github.jonathan3467.music.basedatos.EditorDePerformers
import io.github.jonathan3467.music.basedatos.NombreRepetidoException
import io.github.jonathan3467.music.basedatos.RepositorioJdbc
import io.github.jonathan3467.music.modelo.TipoPerformer
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EditorDePerformersTest {
    private val repo = RepositorioJdbc()
    private val editor = EditorDePerformers(repo)
    private lateinit var dir: File

    @BeforeTest
    fun abrirBaseTemporal(){
        dir = createTempDirectory("editor-test").toFile()
        BaseDeDatos.abrir(File(dir,"musica.db"))
    }

    @AfterTest
    fun cerrarYBorrar(){
        BaseDeDatos.cerrar()
        dir.deleteRecursively()
    }

    @Test
    fun convertirEnGrupoCreaElGrupo(){
        val beatles = repo.insertarPerformer("The Beatles")
        editor.guardar(
            beatles.id, "The Beatles", TipoPerformer.GRUPO,
            datosGrupo = DatosGrupo(inicio = "1960-01-01", fin = "1970-04-10")
        )

        assertEquals(TipoPerformer.GRUPO, repo.buscarPerformerPorId(beatles.id)?.tipo)
        val grupo = assertNotNull(repo.buscarGrupo("The Beatles"))
        assertEquals("1960-01-01", grupo.fechaInicio)
        assertEquals("1970-04-10", grupo.fechaFin)
    }

    @Test
    fun renombrarTambienRenombrarElGrupo(){
        val beatles = repo.insertarPerformer("Beatles")
        editor.guardar(beatles.id, "Beatles", TipoPerformer.GRUPO)
        editor.guardar(beatles.id, "The Beatles", TipoPerformer.GRUPO)
        assertNull(repo.buscarGrupo("Beatles"))
        assertNotNull(repo.buscarGrupo("The Beatles"))
    }

    @Test
    fun cambiarDePersonaAGrupoBorraLaPersona(){
        val blink = repo.insertarPerformer("Blink-182")
        editor.guardar(blink.id, "Blink-182", TipoPerformer.PERSONA)
        assertNotNull(repo.buscarPersona("Blink-182"))
        editor.guardar(blink.id, "Blink-182", TipoPerformer.GRUPO)
        assertNull(repo.buscarPersona("Blink-182"))
        assertNotNull(repo.buscarGrupo("Blink-182"))
    }

    @Test
    fun noSePuedeUsarElNombreDeOtroInterprete(){
        repo.insertarPerformer("The Beatles")
        val otro = repo.insertarPerformer("Beatles")
        assertFailsWith<NombreRepetidoException>{
            editor.guardar(otro.id, "The Beatles", TipoPerformer.GRUPO)
        }

        //la transaccion se deshizo osea nada cambio
        assertEquals("Beatles", repo.buscarPerformerPorId(otro.id)?.nombre)
        assertNull(repo.buscarGrupo("The Beatles"))
    }
}