import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import domain.Nexus
import ui.NexusDetailScreen
import ui.NexusListScreen
import ui.theme.ProtossTheme
import viewmodel.NexusViewModel

fun main() = application {
    val windowState = rememberWindowState(
        width = 800.dp,
        height = 600.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "스타크래프트 2 시간증폭 데모",
        state = windowState,
        resizable = false
    ) {
        ProtossTheme {
            val viewModel = remember { NexusViewModel() }
            var selectedNexus by remember { mutableStateOf<Nexus?>(null) }

            if (selectedNexus == null) {
                NexusListScreen(
                    viewModel = viewModel,
                    onNexusSelected = { nexus ->
                        selectedNexus = nexus
                    }
                )
            } else {
                NexusDetailScreen(
                    viewModel = viewModel,
                    nexus = selectedNexus!!,
                    onBack = { selectedNexus = null }
                )
            }
        }
    }
}
