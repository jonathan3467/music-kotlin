package io.github.jonathan3467.music.basedatos

import java.io.File
import java.sql.SQLException
import kotlin.io.path.createTempDirectory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BaseDeDatosTest {

    private lateinit var dir: File
    private lateinit var archivo: File

    @BeforeTest
    fun abrirBaseTemporal(){
        dir = createTempDirectory("bd-test").toFile()
        archivo = File(dir, "musica.db")
        BaseDeDatos.abrir(archivo)
    }

    @AfterTest
    fun cerrarYBorrar(){
        BaseDeDatos.cerrar()
        dir.deleteRecursively()
    }

    // hace un SELECT COUNT(*) y regresa el numero
    private fun contar(sql: String): Int =
        BaseDeDatos.conexion().createStatement().use { sentencia ->
            sentencia.executeQuery(sql).use { resultado ->
                resultado.next()
                resultado.getInt(1)
            }
        }

    @Test
    fun crearLasSieteTablas(){
        assertEquals(7, contar("SELECT COUNT(*) FROM sqlite_master WHERE type = 'table'"))
    }

    @Test
    fun traeLosTiposYElAlbumDesconocido(){
        assertEquals(3, contar("SELECT COUNT(*) FROM types"))
        assertEquals(1, contar("SELECT COUNT(*) FROM albums WHERE id_album = 0 AND name = 'Unknown'"))
    }

    @Test
    fun reabrirNoDuplicaLosDatos(){
        BaseDeDatos.cerrar()
        BaseDeDatos.abrir(archivo)
        assertEquals(3, contar("SELECT COUNT(*) FROM types"))
    }

    @Test
    fun lasLlavesForaneasEstanActivas(){
        // no existe el performer 999 asi que la base debe de rechazarlo
        assertFailsWith<SQLException>{
            BaseDeDatos.conexion().createStatement().use {
                it.execute(
                    "INSERT INTO rolas (id_performer, id_album, path, title, track, year, genre) " +
                    "VALUES (999, 0, '', 'x', 0, 0, '')"
                )
            }
        }
    }

    @Test
    fun usarLaBaseCerradaFalla(){
        BaseDeDatos.cerrar()
        assertFailsWith<IllegalArgumentException> { BaseDeDatos.conexion() }
    }
}