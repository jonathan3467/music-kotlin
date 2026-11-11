package io.github.jonathan3467.music.controlador

import io.github.jonathan3467.music.Controlador.FormularioRola
import io.github.jonathan3467.music.basedatos.BaseDeDatos
import io.github.jonathan3467.music.basedatos.RepositorioJdbc
import io.github.jonathan3467.music.modelo.Album
import kotlinx.coroutines.CoroutineScope
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ControladorBusquedaTest {

    private val repo = RepositorioJdbc()
    private lateinit var dir: File
    private lateinit var controlador: ControladorPrincipal

    @BeforeTest
    fun preparar(){
        dir = createTempDirectory("controlador-test").toFile()
        BaseDeDatos.abrir(File(dir, "musica.db"))

        val beatles = repo.insertarPerformer("The Beatles")
        val juanes = repo.insertarPerformer("Juanes")
        repo.insertarRola(beatles.id, Album.ID_DESCONOCIDO, "/1.mp3", "Get Back", 1, 1970, "Rock")
        repo.insertarRola(beatles.id, Album.ID_DESCONOCIDO, "/2.mp3", "Help!", 1, 1965, "Rock")
        repo.insertarRola(juanes.id, Album.ID_DESCONOCIDO, "/3.mp3", "A Dios Le Pido", 1, 2002, "Pop")

        controlador = ControladorPrincipal(repo, CoroutineScope(EmptyCoroutineContext))
        controlador.cargarRolas()
    }

    @AfterTest
    fun cerrarYBorrar() {
        BaseDeDatos.cerrar()
        dir.deleteRecursively()
    }

    private fun titulos(): List<String> = controlador.estado.rolas.map { it.rola.titulo }

    @Test
    fun buscarFiltraLaLista(){
        controlador.buscar("interprete: beatles")
        assertEquals(listOf("Get Back", "Help!"), titulos())
        assertNull(controlador.estado.errorBusqueda)
    }

    @Test
    fun unaBusquedaMalEscritaMuestraElErorSinBorrarLaLista(){
        controlador.buscar("año >")
        assertNotNull(controlador.estado.errorBusqueda)
        assertEquals(3, titulos().size)
    }

    @Test
    fun limpiarRegresaTodasLasRolas(){
        controlador.buscar("interprete: juanes")
        controlador.limpiarBusqueda()
        assertEquals(3, titulos().size)
        assertEquals("", controlador.estado.busqueda)
    }

    @Test
    fun buscarAlgoVacioEsComoLimpiar(){
        controlador.buscar("interprete: juanes")
        controlador.buscar("   ")
        assertEquals(3, titulos().size)
    }

    @Test
    fun editarConUnaBusaquedaActivaLaRespesta(){
        controlador.buscar("interprete: beatles")
        val help = controlador.estado.rolas.first { it.rola.titulo == "Help!" }
        controlador.seleccionar(help.rola.id)
        controlador.editarRolaSeleccionada()
        controlador.guardarRola(FormularioRola("Help!", "The Beatles", "1", "1965", "Pop rock"))
        //sigue mostando solo las rolas de los beatles no las tres
        assertEquals(listOf("Get Back", "Help!"),titulos())
    }
}