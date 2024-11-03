package domain

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Nexus(
    val number: Int
) {

    private val _productionState = MutableStateFlow<ProductionState>(ProductionState.Idle)
    val productionState: StateFlow<ProductionState> = _productionState.asStateFlow()

    private val _productionQueue = MutableStateFlow<List<ProbeQueueItem>>(emptyList())
    val productionQueue: StateFlow<List<ProbeQueueItem>> = _productionQueue.asStateFlow()

    private val _probeCount = MutableStateFlow(0)
    val probeCount: StateFlow<Int> = _probeCount.asStateFlow()

    private val _chronoBoostState = MutableStateFlow(ChronoBoostState())
    val chronoBoostState: StateFlow<ChronoBoostState> = _chronoBoostState.asStateFlow()

    var productionJob: Job? = null
    var chronoBoostJob: Job? = null

    fun updateProductionState(state: ProductionState) {
        _productionState.value = state
    }

    fun updateQueue(queue: List<ProbeQueueItem>) {
        _productionQueue.value = queue
    }

    fun incrementProbeCount() {
        _probeCount.value += 1
    }

    fun updateChronoBoostState(state: ChronoBoostState) {
        _chronoBoostState.value = state
    }
}
