package io.github.jonathan3467.music.basedatos

import java.io.File
import java.net.ConnectException
import java.sql.Connection
import java.sql.DriverManager

//la unica conexion a la base de datos, al ser object kotlin asegura que solo existe una instacia
object BaseDeDatos {

    private var conexion: Connection? = null
    // donde va a vivir la base noramlmente
    fun rutaPorOmision(): File =
        File(System.getProperty("user.home"), ".local/share/music-kotlin/musica.db")

    //abre la base en el archivo dado o la crea si no existe
    // en caso de haber una abierta la cierra primero
    //recibe la ruta como parametro para los test
    fun abrir(archivo: File = rutaPorOmision()){
        cerrar()
        archivo.absoluteFile.parentFile?.mkdirs()

        val nueva = DriverManager.getConnection("jdbc:sqlite:${archivo.absolutePath}")
        try {
            //activamos o prendemos o sinonimos de activar las llaves foraneas
            nueva.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }
            if (!tieneTablas(nueva)){
                crearTablas(nueva)
            }
        } catch (e: Exception){
            nueva.close()
            throw e
        }
        conexion = nueva
    }

    //la conexion abierta, falla si nadie llamo abrir antes
    fun conexion(): Connection =
        conexion ?: throw IllegalArgumentException("La base de datos no esta abierta")

    fun cerrar() {
        conexion?.close()
        conexion = null
    }

    // revisa si la base ya tiene el esquema, buscando la tabla rolas
    private fun tieneTablas(c: Connection): Boolean =
        c.prepareStatement(
            "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'rolas'"
        ).use { sentencia ->
            sentencia.executeQuery().use { resultado -> resultado.next() }
        }

    //se crea todo el esquema o nada
    private fun crearTablas(c: Connection){
        c.autoCommit = false
        try {
            c.createStatement().use { sentencia ->
                for (sql in sentenciasDelEsquema()){
                    sentencia.execute(sql)
                }
            }
            c.commit()
        } catch (e: Exception){
            c.rollback()
            throw e
        } finally {
            c.autoCommit = true
        }
    }

}