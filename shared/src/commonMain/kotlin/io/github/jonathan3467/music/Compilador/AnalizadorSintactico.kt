package io.github.jonathan3467.music.Compilador

//los nombres que el usuario puede escribir para cada campo(con o sin acentos)
private val CAMPOS_TEXTO = mapOf(
    "titulo" to CampoTexto.TITULO, "título" to CampoTexto.TITULO,
    "interprete" to CampoTexto.INTERPRETE, "intérprete" to CampoTexto.INTERPRETE,
    "album" to CampoTexto.ALBUM, "álbum" to CampoTexto.ALBUM,
    "genero" to CampoTexto.GENERO, "género" to CampoTexto.GENERO
)

private val CAMPOS_NUMERO = mapOf(
    "año" to CampoNumero.YEAR, "anio" to CampoNumero.YEAR, "year" to CampoNumero.YEAR,
    "pista" to CampoNumero.PISTA
)

//acomoda los tokens de un arbol
//La gramatica (cada renglon es una funcion):
// expresion  termino (O termino)
// termino  factor ([Y] factor)  (dos condiciones seguidas tambien son Y)
// factor No factor | (expresion) | condicion
// condicion campo : valor | campo comparador numero | valor
class AnalizadorSintactico(private val tokens: List<Token>) {

    //el token en el que vamos
    private var i = 0

    fun analizar(): Expresion{
        i = 0
        if (tokens.isEmpty()){
            throw ErrorDeSintaxis("La busqueda esta vacia")
        }
        val expresion = leerExpresion()
        //si sobro (por ejemplo un ")" de mas) la busqueda esta mal escrita
        val sobrante = actual()
        if (sobrante != null){
            throw ErrorDeSintaxis("No se esperaba lo que hay en la posicion ${sobrante.posicion + 1}")
        }
        return expresion
    }

    // expresion  termino (O termino)
    private fun leerExpresion(): Expresion{
        var resultado = leerTermino()
        while (actual() is Token.O){
            i++
            resultado = Expresion.O(resultado, leerTermino())
        }
        return resultado
    }

    // termino  factor ([Y] factor)
    private fun leerTermino(): Expresion{
        var resultado = leerFactor()
        while (true){
            when{
                actual() is Token.Y -> {
                    i++
                    resultado = Expresion.Y(resultado, leerFactor())
                }
                //"let it be" sin comillas tres palabras seguidas se toman como Y
                empiezaFactor(actual()) -> {
                    resultado = Expresion.Y(resultado, leerFactor())
                }
                else -> return resultado
            }
        }
    }

    // factor  NO factor | (expresion) | condicion
    private fun leerFactor(): Expresion {
        val token = actual() ?: throw ErrorDeSintaxis("La busqueda se termina antes de tiempo")
        return when (token) {
            is Token.No -> {
                i++
                Expresion.No(leerFactor())
            }
            is Token.AbreParentesis -> {
                i++
                val adentro = leerExpresion()
                if (actual() !is Token.CierraParentesis){
                    throw ErrorDeSintaxis(
                        "Falta cerrar el parentesis que abren en la posicion ${token.posicion + 1}"
                    )
                }
                i++
                adentro
            }
            else -> leerCondicion()
        }
    }

    //condicion  campo: valor | campo comparador numero | valor
    private fun leerCondicion(): Expresion{
        val token = actual()
        val siguiente = tokens.getOrNull(i + 1)
        val esCampo = token is Token.Palabra &&
                (siguiente is Token.DosPuntos || siguiente is Token.Comparador)

        return if (esCampo){
            leerCondicionDeCampo(token as Token.Palabra)
        } else {
            Expresion.EnTodo(leerValor())
        }
    }

    private fun leerCondicionDeCampo(campo: Token.Palabra): Expresion{
        val nombre = campo.texto.lowercase()
        i++         //el nombre del campo
        val signo = actual()  //el ":" o el comparador  (ya sabemos que esta)
        i++

        CAMPOS_TEXTO[nombre]?.let { campoTexto ->
            if (signo !is Token.DosPuntos){
                throw ErrorDeSintaxis(
                    "El campo\"${campo.texto}\" se busca con dos puntos, por ejemplo ${campo.texto}: beatles"
                )
            }
            return Expresion.Contiene(campoTexto, leerValor())
        }

        CAMPOS_NUMERO[nombre]?.let { campoNumero ->
            // año: 1960  se toma como año = 1960
            val operador = if (signo is Token.Comparador) operadorDe(signo.operador) else Operador.IGUAL
            return Expresion.Compara(campoNumero, operador, leerNumero())
        }

        throw ErrorDeSintaxis(
            "No existe el campo \"${campo.texto}\". Los campos son: titulo, interprete, album, genero, año y pista"
        )
    }

    //una palabra o un texto entre comillas
    private fun leerValor(): String{
        val texto = when (val token = actual()){
            is Token.Palabra -> token.texto
            is Token.Cadena -> token.texto
            null -> throw ErrorDeSintaxis("Falta el texto a buscar al final")
            else -> throw ErrorDeSintaxis("Se esperaba un texto en la posicion ${token.posicion + 1}")
        }
        i++
        return texto
    }

    private fun leerNumero(): Int{
        val token = actual()
        val numero = (token as? Token.Palabra)?.texto?.toIntOrNull()
            ?: throw ErrorDeSintaxis(
                if (token == null) "Falta un numero al final"
                else "Se esperaba un numero en la posicion ${token.posicion + 1}"
            )
        i++
        return numero
    }

    private fun actual(): Token? = tokens.getOrNull(i)

    private fun empiezaFactor(token: Token?): Boolean =
        token is Token.Palabra || token is Token.Cadena ||
                token is Token.No || token is Token.AbreParentesis

    private fun operadorDe(simbolo: String): Operador =
        Operador.entries.first { it.simbolo == simbolo }
}

// del texto que escribe el usuario al arbol
fun analizarBusqueda(texto: String): Expresion =
    AnalizadorSintactico(AnalizadorLexico(texto).analizar()).analizar()