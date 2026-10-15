package io.github.jonathan3467.music.basedatos

import kotlin.test.Test
import kotlin.test.assertEquals

class EsquemaTest {
    @Test
    fun separaLasOnceSentencias(){
        assertEquals(11,sentenciasDelEsquema().size)
    }
}