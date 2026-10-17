package io.github.jonathan3467.music.basedatos

import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.Performer
import io.github.jonathan3467.music.modelo.Rola
import io.github.jonathan3467.music.modelo.TipoPerformer
import java.sql.Connection
import java.sql.ResultSet

class RepositorioJdbc : RepositorioMusica{

    //se pide para usar siempre la conexion abierta actual
    private val conexion: Connection
        get() = BaseDeDatos.conexion()

    override fun buscarPerformerPorNombre(nombre: String): Performer? =
        conexion.prepareStatement(
            "SELECT id_performer, id_type, name FROM performers WHERE name = ?"
        ).use { sentencia ->
            sentencia.setString(1, nombre)
            sentencia.executeQuery().use { r ->
                if (r.next()) leerPerformer(r) else null
            }
        }

    override fun insertarPerformer(nombre: String, tipo: TipoPerformer): Performer {
        conexion.prepareStatement(
            "INSERT INTO performers (id_type, name) VALUES (?, ?)"
        ).use { sentencia ->
            sentencia.setInt(1, tipo.id)
            sentencia.setString(2, nombre)
            sentencia.executeUpdate()
        }
        return Performer(ultimoId(), tipo, nombre)
    }

    override fun buscarAlbum(nombre: String, ruta: String): Album? =
        conexion.prepareStatement(
            "SELECT id_album, path, name, year FROM albums WHERE name = ? AND path = ?"
        ).use { sentencia ->
            sentencia.setString(1, nombre)
            sentencia.setString(2, ruta)
            sentencia.executeQuery().use { r ->
                if (r.next()) leerAlbum(r) else null
            }
        }

    override fun insertarAlbum(nombre: String, ruta: String, year: Int): Album {
        conexion.prepareStatement(
            "INSERT INTO albums (path, name, year) VALUES (?, ?, ?)"
        ).use { sentencia ->
            sentencia.setString(1,ruta)
            sentencia.setString(2, nombre)
            sentencia.setInt(3, year)
            sentencia.executeUpdate()
        }
        return Album(ultimoId(), ruta, nombre, year)
    }

    override fun insertarRola(
        idPerformer: Long,
        idAlbum: Long,
        ruta: String,
        titulo: String,
        pista: Int,
        year: Int,
        genero: String
    ): Rola {
        conexion.prepareStatement(
            "INSERT INTO rolas (id_performer, id_album, path, title, track, year, genre) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)"
        ).use { sentencia ->
            sentencia.setLong(1, idPerformer)
            sentencia.setLong(2, idAlbum)
            sentencia.setString(3, ruta)
            sentencia.setString(4, titulo)
            sentencia.setInt(5, pista)
            sentencia.setInt(6, year)
            sentencia.setString(7, genero)
            sentencia.executeUpdate()
        }
        return Rola(ultimoId(), idPerformer, idAlbum, ruta, titulo, pista, year, genero)
    }

    override fun todasLasRolas(): List<Rola> =
        conexion.prepareStatement(
            "SELECT id_rola, id_performer, id_album, path, title, track, year, genre " +
            "FROM rolas ORDER BY title COLLATE NOCASE"
        ).use { sentencia ->
            sentencia.executeQuery().use { r ->
                val rolas = mutableListOf<Rola>()
                while (r.next()){
                    rolas.add(leerRola(r))
                }
                rolas
            }
        }

    override fun vaciar() {
        enTransaccion {
            conexion.createStatement().use { sentencia ->
                //primero lo que apunta a otras tablas y luego lo apuntado o a las que les apuntan
                sentencia.executeUpdate("DELETE FROM in_group")
                sentencia.executeUpdate("DELETE FROM rolas")
                sentencia.executeUpdate("DELETE FROM persons")
                sentencia.executeUpdate("DELETE FROM groups")
                sentencia.executeUpdate("DELETE FROM performers")
                sentencia.executeUpdate("DELETE FROM albums WHERE id_album <> ${Album.ID_DESCONOCIDO}")
            }
        }
    }

    override fun <T> enTransaccion(bloque: () -> T): T {
        val c = conexion
        // si ya estamos adentro de una transaccion solo se ejecuta el bloque
        // la transaccion de afuera es la que decide si todo o nadota o nada de nada
        if (!c.autoCommit){
            return bloque()
        }
        c.autoCommit = false
        try {
            val resultado = bloque()
            c.commit()
            return resultado
        } catch (e: Exception){
            c.rollback()
            throw e
        } finally {
            c.autoCommit = true
        }
    }

    //el id que sql le asigno o le puso al ultimo INSERT de esta conexion
    private fun ultimoId(): Long =
        conexion.createStatement().use { sentencia ->
            sentencia.executeQuery("SELECT last_insert_rowid()").use { r ->
                r.next()
                r.getLong(1)
            }
        }

    // conviernte un renglon en un objeto, si viene NULL le ponemos ""
    private fun leerPerformer(r: ResultSet) = Performer(
        id = r.getLong("id_performer"),
        tipo = TipoPerformer.desdeId(r.getInt("id_type")),
        nombre = r.getString("name") ?: ""
    )

    private fun leerAlbum(r: ResultSet) = Album(
        id = r.getLong("id_album"),
        ruta = r.getString("path") ?: "",
        nombre = r.getString("name") ?: "",
        year = r.getInt("year")
    )

    private fun leerRola(r: ResultSet) = Rola(
        id = r.getLong("id_rola"),
        idPerformer = r.getLong("id_performer"),
        idAlbum = r.getLong("id_album"),
        ruta = r.getString("path") ?: "",
        titulo = r.getString("title") ?: "",
        pista = r.getInt("track"),
        year = r.getInt("year"),
        genero = r.getString("genre") ?: ""
    )
}