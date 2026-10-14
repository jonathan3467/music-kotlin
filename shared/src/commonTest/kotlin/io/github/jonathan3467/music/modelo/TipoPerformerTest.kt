package io.github.jonathan3467.music.Modelo

import io.github.jonathan3467.music.modelo.TipoPerformer
import kotlin.test.Test
import kotlin.test.assertEquals

class TipoPerformerTest {

    @Test
    fun convierteIdsDelEsquema(){
        assertEquals(TipoPerformer.PERSONA, TipoPerformer.desdeId(0))
        assertEquals(TipoPerformer.GRUPO, TipoPerformer.desdeId(1))
        assertEquals(TipoPerformer.DESCONOCIDO, TipoPerformer.desdeId(2))
    }

    @Test
    fun idDesconocidoRegresaDesconocido(){
        assertEquals(TipoPerformer.DESCONOCIDO, TipoPerformer.desdeId(99))
    }
}