package io.github.jonathan3467.music.basedatos

import io.github.jonathan3467.music.modelo.Album
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
import kotlin.test.assertTrue

class RepositorioJdbcTest {

    private val repo = RepositorioJdbc()
    private lateinit var dir: File

    @BeforeTest
    fun abrirBaseTemporal(){
        dir = createTempDirectory("repo-test").toFile()
        BaseDeDatos.abrir(File(dir, "musica.db"))
    }

    @AfterTest
    fun cerrarYBorrar(){
        BaseDeDatos.cerrar()
        dir.deleteRecursively()
    }

    @Test
    fun insertaYBuscaUnPerformer(){
        val insertado = repo.insertarPerformer("The Beatles")
        val encontrado = repo.buscarPerformerPorNombre("The Beatles")
        assertEquals(insertado, encontrado)
        assertEquals(TipoPerformer.DESCONOCIDO, encontrado?.tipo)
    }

    @Test
    fun performerQueNoExisteEsNull(){
        assertNull(repo.buscarPerformerPorNombre("Nadie te quiere todos te odian"))
    }

    @Test
    fun albumesConMismoNombreEnDistintaCarpetaSonDistintos(){
        repo.insertarAlbum("Mejores Exitos", "/musica/queen", 1981)
        assertNotNull(repo.buscarAlbum("Mejores Exitos", "/musica/queen"))
        assertNull(repo.buscarAlbum("Mejores Exitos", "/musica/abba"))
    }

    @Test
    fun insertaYListaRolas(){
        val performer = repo.insertarPerformer("The Beatles")
        val album = repo.insertarAlbum("Let It Be", "/musica/let-it-be", 1970)

        val rola = repo.insertarRola(
            idPerformer = performer.id,
            idAlbum = album.id,
            ruta = "/musica/let-it-be/across.mp3",
            titulo = "Across The Universe",
            pista = 3,
            year = 1970,
            genero = "Rock"
        )
        assertEquals(listOf(rola), repo.todasLasRolas())
    }

    @Test
    fun vaciarDejaSoloElAlbumDesconocido(){
        val perfomer = repo.insertarPerformer("Juanes")
        repo.insertarAlbum("Un Dia Normal", "/musica", 2002)
        repo.insertarRola(perfomer.id, Album.ID_DESCONOCIDO, "/a.mp3", "A Dios Le Pido", 1, 2002, "Pop")
        repo.vaciar()
        assertTrue(repo.todasLasRolas().isEmpty())
        assertNull(repo.buscarPerformerPorNombre("Juanes"))
        assertNull(repo.buscarAlbum("Un Dia Normal", "/musica"))
        assertNotNull(repo.buscarAlbum("Unknown", ""))
    }

    @Test
    fun silaTransaccionFallaNoSeGuardaNada(){
        assertFailsWith<IllegalStateException> {
            repo.enTransaccion {
                repo.insertarPerformer("Fantasma")
                error("algo salio mal")
            }
        }
        assertNull(repo.buscarPerformerPorNombre("Fantasma"))
    }

    @Test
    fun lasRolasDetalladasTraenLosNombres(){
        val performer = repo.insertarPerformer("The Beatles")
        val album = repo.insertarAlbum("Let It Be", "/musica/let-it-be",1970)
        repo.insertarRola(performer.id, album.id, "/musica/let-it-be/get-back.mp3", "Get Back", 1, 1970, "Rock")

        val detallada = repo.todasLasRolasDetalladas().single()
        assertEquals("Get Back", detallada.rola.titulo)
        assertEquals("The Beatles", detallada.performer)
        assertEquals("Let It Be", detallada.album)
    }

    @Test
    fun actualizaUnaRolaSinTocarLasDemas(){
        val beatles = repo.insertarPerformer("The Beatles")
        val juanes = repo.insertarPerformer("Juanes")
        val rola = repo.insertarRola(beatles.id, Album.ID_DESCONOCIDO, "/a.mp3", "A Dios Le Pid", 0, 0, "")
        val otra = repo.insertarRola(beatles.id, Album.ID_DESCONOCIDO, "/b.mp3", "Let It Be", 6, 1970, "Rock")
        val corregida = rola.copy(
            idPerformer = juanes.id,
            titulo = "A Dios Le Pido",
            pista = 1,
            year = 2002,
            genero = "Pop"
        )
        repo.actualizarRola(corregida)
        val enBase = repo.todasLasRolas().associateBy { it.id }
        assertEquals(corregida, enBase[rola.id])
        assertEquals(otra, enBase[otra.id])
    }

    @Test
    fun actualizaUnAlbum(){
        val album = repo.insertarAlbum("Let It Bee", "/musica/let-it-be", 0)
        repo.actualizarAlbum(album.copy(nombre = "Let It Be", year = 1970))
        assertEquals(album.copy(nombre = "Let It Be", year = 1970), repo.buscarAlbumPorId(album.id))
    }

    @Test
    fun obtenerOCrearReutilizaOCrea(){
        val existente = repo.insertarPerformer("The Beatles")
        assertEquals(existente, repo.obtenerOCrearPerformer("The Beatles"))

        val nuevo = repo.obtenerOCrearPerformer("Juanes")
        assertEquals(nuevo, repo.buscarPerformerPorNombre("Juanes"))
    }
}