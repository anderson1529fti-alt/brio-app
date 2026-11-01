package com.brio.gym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brio.gym.ui.BrioViewModel
import com.brio.gym.ui.screens.PantallaAjustes
import com.brio.gym.ui.screens.PantallaArranque
import com.brio.gym.ui.screens.PantallaCasa
import com.brio.gym.ui.screens.PantallaEntreno
import com.brio.gym.ui.screens.PantallaHistorial
import com.brio.gym.ui.screens.PantallaMarcas
import com.brio.gym.ui.theme.TemaBrio

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaBrio {
                var arrancando by rememberSaveable { mutableStateOf(true) }
                if (arrancando) {
                    PantallaArranque(alTerminar = { arrancando = false })
                } else {
                    AppBrio()
                }
            }
        }
    }
}

private enum class Pestana(val titulo: String, val icono: ImageVector) {
    CASA("Hoy", Icons.Filled.Home),
    HISTORIAL("Historial", Icons.Filled.List),
    MARCAS("Récords", Icons.Filled.Star),
    AJUSTES("Ajustes", Icons.Filled.Settings),
}

@Suppress("DEPRECATION") // Icons.Filled.List: en versiones nuevas pasa a AutoMirrored
@Composable
private fun AppBrio(vm: BrioViewModel = viewModel()) {
    val datos by vm.datos.collectAsStateWithLifecycle()
    val frase by vm.frase.collectAsStateWithLifecycle()
    val animo by vm.animo.collectAsStateWithLifecycle()
    val descanso by vm.descanso.collectAsStateWithLifecycle()
    var pestana by rememberSaveable { mutableStateOf(Pestana.CASA) }

    val entrenando = datos.activo != null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!entrenando) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    Pestana.entries.forEach { p ->
                        NavigationBarItem(
                            selected = pestana == p,
                            onClick = { pestana = p },
                            icon = { Icon(p.icono, contentDescription = p.titulo) },
                            label = { Text(p.titulo) },
                        )
                    }
                }
            }
        },
    ) { relleno ->
        val mod = Modifier.fillMaxSize().padding(relleno)
        if (entrenando) {
            PantallaEntreno(datos, frase, animo, descanso, vm, mod)
        } else when (pestana) {
            Pestana.CASA -> PantallaCasa(
                datos = datos,
                frase = frase,
                animo = animo,
                alTocarBrio = vm::tocarBrio,
                alEmpezar = vm::empezarEntreno,
                alPedirConsejo = vm::decir,
                modifier = mod,
            )
            Pestana.HISTORIAL -> PantallaHistorial(datos.entrenos, vm::borrarEntreno, mod)
            Pestana.MARCAS -> PantallaMarcas(datos.entrenos, mod)
            Pestana.AJUSTES -> PantallaAjustes(
                ajustes = datos.ajustes,
                alCambiarTono = vm::cambiarTono,
                alCambiar = vm::cambiarAjustes,
                alProbar = { vm.tocarBrio(); pestana = Pestana.CASA },
                modifier = mod,
            )
        }
    }
}
