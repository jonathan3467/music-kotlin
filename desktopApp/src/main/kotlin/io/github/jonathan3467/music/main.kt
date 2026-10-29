package io.github.jonathan3467.music

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.jonathan3467.music.Vista.PantallaPrincipal
import io.github.jonathan3467.music.basedatos.BaseDeDatos
import io.github.jonathan3467.music.basedatos.RepositorioJdbc
import io.github.jonathan3467.music.controlador.ControladorPrincipal
import java.awt.Component
import java.io.File
import javax.swing.JFileChooser

fun main() {
    BaseDeDatos.abrir()

    application {
        val alcance = rememberCoroutineScope()
        val controlador = remember {
            ControladorPrincipal(RepositorioJdbc(), alcance).apply { cargarRolas() }
        }

        Window(
            onCloseRequest = {
                BaseDeDatos.cerrar()
                exitApplication()
            },
            title = "Rolas"
        ) {
            PantallaPrincipal(
                estado = controlador.estado,
                alMinar = { elegirCarpeta(window)?.let { controlador.minar(it)}},
                alSeleccionar = controlador::seleccionar
            )
        }
    }
}

// abre el dialogo para escoger una carpeta: regresa null si el usuario cancela
private fun elegirCarpeta(padre: Component): File? {
    val selector = JFileChooser(System.getProperty("user.home")).apply {
        dialogTitle = "Escoge la carpeta de musica"
        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    }
    return if (selector.showOpenDialog(padre) == JFileChooser.APPROVE_OPTION){
        selector.selectedFile
    } else{
        null
    }
}