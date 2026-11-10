package io.github.jonathan3467.music.Compilador

//el resultado del compilador, el texto que va despues del WHERE
// y los calores que van en cada ? en orden
data class ConsultaSql(
    val where: String,
    val parametros: List<Any>
)

// recorre el arbol y escribe el SQL, las columnas usan los apodos de la consulta de rolas detalladas
// r = rolas, p = performers, a = albums
fun generarSql(expresion: Expresion): ConsultaSql{
    val parametros = mutableListOf<Any>()
    val where = escribir(expresion, parametros)
    return ConsultaSql(where, parametros)
}

// todo el compilador del texto del usuario al SQL
fun compilarBusqueda(texto: String): ConsultaSql =
    generarSql(analizarBusqueda(texto))

//escribe el SQL de un nodo y de todos sus hijos
// cada que escribe un ?, agrega su valor a parametros en el mismo orden
private fun escribir(expresion: Expresion, parametros: MutableList<Any>): String =
    when(expresion){
        is Expresion.Y ->
            "(${escribir(expresion.izquierdo, parametros)} AND ${escribir(expresion.derecha, parametros)})"

        is Expresion.O ->
            "(${escribir(expresion.izquierdo, parametros)} OR ${escribir(expresion.derecha, parametros)})"

        is Expresion.No ->
            "NOT (${escribir(expresion.expresion, parametros)})"

        is Expresion.Contiene ->
            contiene(columnaDe(expresion.campo), expresion.texto, parametros)

        is Expresion.Compara -> {
            parametros.add(expresion.numero)
            "${columnaDe(expresion.campo)} ${expresion.operador.simbolo} ?"
        }

        is Expresion.EnTodo -> {
            val enTitulo = contiene("r.title", expresion.texto, parametros)
            val enInterprete = contiene("p.name", expresion.texto, parametros)
            val enAlbum = contiene("a.name", expresion.texto, parametros)
            "($enTitulo OR $enInterprete OR $enAlbum)"
        }
    }

// columna LIKE '%texto%' : la columna contiene al texto, sin importar mayusculas
private fun contiene(columna: String, texto: String, parametros: MutableList<Any>): String {
    parametros.add("%${escaparComodines(texto)}%")
    return "$columna LIKE ? ESCAPE '\\'"
}

//en LIKE, % y _ son comodines, si el usuario busca "100%"
//el % debe de ser un % de verdad, asi que se escapa con \
private fun escaparComodines(texto: String): String =
    texto.replace("\\", "\\\\")
        .replace("%", "\\%")
        .replace("_", "\\_")

private fun columnaDe(campo: CampoTexto): String = when (campo){
    CampoTexto.TITULO -> "r.title"
    CampoTexto.INTERPRETE -> "p.name"
    CampoTexto.ALBUM -> "a.name"
    CampoTexto.GENERO -> "r.genre"
}

private fun columnaDe(campo: CampoNumero): String = when (campo){
    CampoNumero.YEAR -> "r.year"
    CampoNumero.PISTA -> "r.track"
}