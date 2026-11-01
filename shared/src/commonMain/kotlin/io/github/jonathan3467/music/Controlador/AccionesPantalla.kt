package io.github.jonathan3467.music.Controlador

interface AccionesPantalla {
    fun seleccionar(idRola: Long)
    fun editarRolaSeleccionada()
    fun editarAlbumSeleccionado()
    fun guardarRola(formulario: FormularioRola)
    fun guardarAlbum(formulario: FormularioAlbum)
    fun cerrarDialogo()
}