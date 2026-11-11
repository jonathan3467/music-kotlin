package io.github.jonathan3467.music.Controlador

interface AccionesPantalla {
    fun seleccionar(idRola: Long)
    fun editarRolaSeleccionada()
    fun editarAlbumSeleccionado()
    fun guardarRola(formulario: FormularioRola)
    fun guardarAlbum(formulario: FormularioAlbum)
    fun cerrarDialogo()
    fun editarPerformerSeleccionado()
    fun guardarPerformer(formulario: FormularioPerformer)
    fun verMiembros()
    fun agregarMiembro(nombre: String)
    fun quitarMiembro(idPersona: Long)
    fun nuevaPersona()
    fun guardarNuevaPersona(formulario: FormularioPersona)
    fun buscar(texto: String)
    fun limpiarBusqueda()
}