package io.github.jonathan3467.music.basedatos

import io.github.jonathan3467.music.modelo.Album
import io.github.jonathan3467.music.modelo.Performer
import io.github.jonathan3467.music.modelo.Rola
import io.github.jonathan3467.music.modelo.RolaDetallada
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

    override fun buscarAlbumPorId(id: Long): Album? =
        conexion.prepareStatement(
            "SELECT id_album, path, name, year FROM albums WHERE id_album = ?"
        ).use { sentencia ->
            sentencia.setLong(1,id)
            sentencia.executeQuery().use { r ->
                if (r.next()) leerAlbum(r) else null
            }
        }

    override fun actualizarRola(rola: Rola) {
        conexion.prepareStatement(
            "UPDATE rolas SET id_performer = ?, title = ?, track = ?, year = ?, genre = ? " +
            "WHERE id_rola = ?"
        ).use { sentencia ->
            sentencia.setLong(1,rola.idPerformer)
            sentencia.setString(2,rola.titulo)
            sentencia.setInt(3,rola.pista)
            sentencia.setInt(4,rola.year)
            sentencia.setString(5,rola.genero)
            sentencia.setLong(6,rola.id)
            sentencia.executeUpdate()
        }
    }

    override fun actualizarAlbum(album: Album) {
        conexion.prepareStatement(
            "UPDATE albums SET name = ?, year = ? WHERE id_album = ?"
        ).use { sentencia ->
            sentencia.setString(1, album.nombre)
            sentencia.setInt(2, album.year)
            sentencia.setLong(3, album.id)
            sentencia.executeUpdate()
        }
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

    override fun todasLasRolasDetalladas(): List<RolaDetallada> =
        conexion.prepareStatement(
            "SELECT r.id_rola, r.id_performer, r.id_album, r.path, r.title, " +
            "R.track, r.year, r.genre, p.name AS performer, a.name AS album " +
            "FROM rolas r " +
            "JOIN performers p ON p.id_performer = r.id_performer " +
            "JOIN albums a ON a.id_album = r.id_album " +
            "ORDER BY r.title COLLATE NOCASE"
        ).use { sentencia ->
            sentencia.executeQuery().use { r ->
                val rolas = mutableListOf<RolaDetallada>()
                while (r.next()){
                    rolas.add(
                        RolaDetallada(
                            rola = leerRola(r),
                            performer = r.getString("performer") ?: "",
                            album = r.getString("album") ?: ""
                        )
                    )
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