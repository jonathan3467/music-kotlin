package io.github.jonathan3467.music.controlador

import io.github.jonathan3467.music.Controlador.FormularioAlbum
import io.github.jonathan3467.music.Controlador.FormularioPerformer
import io.github.jonathan3467.music.Controlador.FormularioPersona
import io.github.jonathan3467.music.Controlador.FormularioRola
import io.github.jonathan3467.music.Controlador.esFechaValida
import io.github.jonathan3467.music.modelo.TipoPerformer
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FormulariosTest {
    private val valido = FormularioRola("Get Back", "The Beatles", "1", "1970", "Rock")

    @Test
    fun unFormulariosCoreectoNoTieneError(){
        assertNull(valido.error())
    }

    @Test
    fun elTituloNoPuedeQuedarVacio(){
        assertNotNull(valido.copy(titulo = "   ").error())
    }

    @Test
    fun laPistaDebeSerNumero(){
        assertNotNull(valido.copy(pista = "uno").error())
    }

    @Test
    fun elYearNoPuedeSerNegativo(){
        assertNotNull(valido.copy(year = "-5").error())
    }

    @Test
    fun albumSinNombreTieneError(){
        assertNotNull(FormularioAlbum("", "1970").error())
    }

    @Test
    fun reconoceFechasValidas(){
        assertTrue(esFechaValida("1096-01-01"))
        assertTrue(esFechaValida("2024-02-29")) // bisisesto
        assertFalse(esFechaValida("2023-02-29")) // no bisiesto
        assertFalse(esFechaValida("1960-13-01")) //mes 13
        assertFalse(esFechaValida("1960-1-1")) //sin ceros
        assertFalse(esFechaValida("ayer"))
    }

    @Test
    fun unGrupoNoPUedeTerminarAntesDeEmpezar(){
        val f = FormularioPerformer("The Beatles", TipoPerformer.GRUPO, inicio = "1970-01-01", fin = "1960-01-01")
        assertNotNull(f.error())
    }

    @Test
    fun unaPersonaConFechaMalEscritaTieneError(){
        val f = FormularioPerformer("Juanes", TipoPerformer.PERSONA, nacimiento = "9 de agosto")
        assertNotNull(f.error())
    }

    @Test
    fun lasFechasPuedenQuedarVacias(){
        assertNull(FormularioPerformer("Juanes", TipoPerformer.PERSONA).error())
    }

    @Test
    fun siEsDesconocidoNoSeRevisanFechas(){
        val f = FormularioPerformer("alguien", TipoPerformer.DESCONOCIDO, inicio = "basura")
        assertNull(f.error())
    }

    @Test
    fun unaPersonaNuevaNecesitaNombre(){
        assertNotNull(FormularioPersona("  ").error())
    }

    @Test
    fun unaPersonaNuevaRevisaSusFechas(){
        assertNotNull(FormularioPersona("Ringo Starr", nacimiento = "1940-07-32").error())
        assertNull(FormularioPersona("Ringo Starr", nacimiento = "1940-07-07").error())
    }
}