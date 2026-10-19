package io.github.jonathan3467.music.minero

import io.github.jonathan3467.music.basedatos.BaseDeDatos
import io.github.jonathan3467.music.basedatos.RepositorioJdbc
import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.RolaMinada
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ImportadorTest {
    private val repo = RepositorioJdbc()
    private val importador = Importador(repo)
    private lateinit var dir: File

    @BeforeTest
    fun abrirBaseTemporal(){
        dir = createTempDirectory("importador-test").toFile()
        BaseDeDatos.abrir(File(dir, "musica.db"))
    }

    @AfterTest
    fun cerrarYBorrar(){
        BaseDeDatos.cerrar()
        dir.deleteRecursively()
    }

    // arma una rola minada de pruba para no escribir todos los campos cada vez
    private  fun  rolaMinada(
        titulo : String,
        interprete: String,
        album: String?,
        rutaAlbum: String = "/musica/$album"
    ) = RolaMinada(
        ruta = "$rutaAlbum/$titulo.mp3",
        rutaAlbum = rutaAlbum,
        titulo = titulo,
        interprete = interprete,
        album = album,
        year = 1970,
        pista = 1,
        genero = "Rock"
    )

    @Test
    fun guardarLasRolasConSusPerformersYAlbumes(){
        val rolas = listOf(
            rolaMinada("Across The Universe", "The Beatles", "Let It Be"),
            rolaMinada("Get Back", "The Beatles", "Let It Be"),
            rolaMinada("A Dios Le Pido", "Juanes", null, rutaAlbum = "/musica/sueltas")
        )

        val guardadas = importador.importar(rolas)
        assertEquals(3, guardadas)
        val beatles = assertNotNull(repo.buscarPerformerPorNombre("The Beatles"))
        val letItBe = assertNotNull(repo.buscarAlbum("Let It Be", "/musica/Let It Be"))
        val enBase = repo.todasLasRolas().associateBy { it.titulo }

        //Las dos rolas de los Beatles apuntan al mismo performer y al mismo album
        assertEquals(beatles.id, enBase.getValue("Across The Universe").idPerformer)
        assertEquals(beatles.id, enBase.getValue("Get Back").idPerformer)
        assertEquals(letItBe.id, enBase.getValue("Get Back").idAlbum)

        //la rola sin album va al album 0
        assertEquals(Album.ID_DESCONOCIDO, enBase.getValue("A Dios Le Pido").idAlbum)
    }

    @Test
    fun volverAImportarBorrarLoAnterior(){
        importador.importar(listOf(rolaMinada("Get Back", "The Beatles", "Let It Be")))
        importador.importar(listOf(rolaMinada("Abracadabra", "Alizée", "Gourmandises")))
        assertEquals(listOf("Abracadabra"), repo.todasLasRolas().map { it.titulo })
        assertNull(repo.buscarPerformerPorNombre("The Beatles"))
    }

    @Test
    fun minaUnaCarpetaYLaGuardar(){
        val musica = File(dir, "musica").apply { mkdirs() }
        File(musica, "Uno.mp3").writeText("x")
        File(musica, "Dos.mp3").writeText("x")
        importador.importar(Minero().minar(musica))

        assertEquals(listOf("Dos", "Uno"), repo.todasLasRolas().map { it.titulo })
        assertNotNull(repo.buscarPerformerPorNombre("Unknown"))
    }
}