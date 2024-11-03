package viewmodel

import domain.Nexus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NexusViewModel {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _nexusList = MutableStateFlow<List<Nexus>>(listOf())
    val nexusList: StateFlow<List<Nexus>> = _nexusList.asStateFlow()

    init {
        addInitialNexus()
    }

    private fun addInitialNexus() {
        _nexusList.value = listOf(Nexus(number = 1))
    }

    fun addNexus() {
        val nextNumber = (_nexusList.value.maxOfOrNull { it.number } ?: 0) + 1
        _nexusList.value = _nexusList.value + Nexus(number = nextNumber)
    }

    fun startProbeProduction(nexus: Nexus) {
        scope.launch {
            nexus.startProduction()
        }
    }

    fun applyChronoBoost(nexus: Nexus) {
        scope.launch {
            nexus.applyChronoBoost()
        }
    }

    fun cancelProduction(nexus: Nexus, index: Int) {
        scope.launch {
            nexus.cancelProduction(index)
        }
    }

    fun shutdown() {
        scope.cancel()
        _nexusList.value.forEach { it.shutdown() }
    }
}
