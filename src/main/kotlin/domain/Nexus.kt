package domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Nexus(
    val number: Int
) {

    companion object {
        const val PROBE_PRODUCTION_TIME = 10000L
        const val CHRONOBOOST_DURATION = 10000L
        const val TIME_ACCELERATION = 3
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

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

    fun startProduction() {
        if (_productionQueue.value.size >= 5) return

        // 큐에 새 항목 추가
        updateQueue(_productionQueue.value + ProbeQueueItem())

        // 생산이 진행중이지 않은 경우에만 시작
        if (_productionState.value is ProductionState.Idle) {
            startProductionProcess()
        }
    }

    private fun startProductionProcess() {
        productionJob = scope.launch {
            while (_productionQueue.value.isNotEmpty()) {
                updateProductionState(ProductionState.Producing(0f))

                var accumulatedProgress = 0f
                var lastUpdateTime = System.currentTimeMillis()

                while (accumulatedProgress < 1f) {
                    val currentTime = System.currentTimeMillis()
                    val deltaTime = currentTime - lastUpdateTime
                    lastUpdateTime = currentTime

                    val isCurrentlyBoosted = _chronoBoostState.value.isActive
                    val progressIncrement = if (isCurrentlyBoosted) {
                        (deltaTime.toFloat() / PROBE_PRODUCTION_TIME) * TIME_ACCELERATION
                    } else {
                        deltaTime.toFloat() / PROBE_PRODUCTION_TIME
                    }

                    accumulatedProgress = (accumulatedProgress + progressIncrement).coerceAtMost(1f)

                    // 큐의 첫 번째 항목 업데이트
                    updateQueue(_productionQueue.value.toMutableList().apply {
                        if (isNotEmpty()) {
                            this[0] = this[0].copy(
                                progress = accumulatedProgress,
                            )
                        }
                    })

                    updateProductionState(ProductionState.Producing(accumulatedProgress))
                    delay(16) // 약 60fps
                }

                incrementProbeCount()
                updateQueue(_productionQueue.value.drop(1))
                updateProductionState(ProductionState.Complete)
                delay(500)

                if (_productionQueue.value.isEmpty()) {
                    updateProductionState(ProductionState.Idle)
                }
            }
            updateProductionState(ProductionState.Idle)
        }
    }

    fun applyChronoBoost() {
        if (_chronoBoostState.value.isActive) return

        chronoBoostJob?.cancel()
        chronoBoostJob = scope.launch {
            updateChronoBoostState(
                ChronoBoostState(
                    isActive = true,
                    remainingTimeMillis = CHRONOBOOST_DURATION
                )
            )

            val startTime = System.currentTimeMillis()

            while (true) {
                delay(100)
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime
                val remainingTime = CHRONOBOOST_DURATION - elapsedTime

                if (remainingTime <= 0) {
                    updateChronoBoostState(ChronoBoostState())
                    break
                }

                updateChronoBoostState(
                    _chronoBoostState.value.copy(
                        remainingTimeMillis = remainingTime
                    )
                )
            }
        }
    }

    fun cancelProduction(index: Int) {
        if (index < 0 || index >= _productionQueue.value.size) return

        val newQueue = _productionQueue.value.toMutableList()
        newQueue.removeAt(index)
        updateQueue(newQueue)

        // 현재 생산 중인 항목을 취소한 경우
        if (index == 0) {
            productionJob?.cancel()
            updateProductionState(ProductionState.Idle)

            // 남은 대기열이 있다면 다시 시작
            if (newQueue.isNotEmpty()) {
                startProductionProcess()
            }
        }
    }

    // 기존 NexusViewModel의 일부 로직을 Nexus로 이동
    fun incrementProbeCount() {
        _probeCount.value += 1
    }

    fun updateProductionState(state: ProductionState) {
        _productionState.value = state
    }

    fun updateQueue(queue: List<ProbeQueueItem>) {
        _productionQueue.value = queue
    }

    fun updateChronoBoostState(state: ChronoBoostState) {
        _chronoBoostState.value = state
    }

    fun shutdown() {
        scope.cancel()
    }
}
