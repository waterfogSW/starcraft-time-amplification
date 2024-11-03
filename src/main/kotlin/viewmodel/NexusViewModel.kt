package viewmodel

import domain.ChronoBoostState
import domain.Nexus
import domain.ProbeQueueItem
import domain.ProductionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NexusViewModel {

    companion object {

        const val PROBE_PRODUCTION_TIME = 10000L
        const val CHRONOBOOST_DURATION = 10000L
        const val TIME_ACCELERATION = 3
    }

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
        _nexusList.update { it + Nexus(number = nextNumber) }
    }

    fun startProbeProduction(nexus: Nexus) {
        if (nexus.productionQueue.value.size >= 5) return

        // 큐에 새 항목 추가
        nexus.updateQueue(nexus.productionQueue.value + ProbeQueueItem())

        // 생산이 진행중이지 않은 경우에만 시작
        if (nexus.productionState.value is ProductionState.Idle) {
            startProductionProcess(nexus)
        }
    }

    private fun startProductionProcess(nexus: Nexus) {
        nexus.productionJob = scope.launch {
            while (nexus.productionQueue.value.isNotEmpty()) {
                nexus.updateProductionState(ProductionState.Producing(0f))

                var accumulatedProgress = 0f
                var lastUpdateTime = System.currentTimeMillis()

                while (accumulatedProgress < 1f) {
                    val currentTime = System.currentTimeMillis()
                    val deltaTime = currentTime - lastUpdateTime
                    lastUpdateTime = currentTime

                    val isCurrentlyBoosted = nexus.chronoBoostState.value.isActive
                    val progressIncrement = if (isCurrentlyBoosted) {
                        (deltaTime.toFloat() / PROBE_PRODUCTION_TIME) * TIME_ACCELERATION
                    } else {
                        deltaTime.toFloat() / PROBE_PRODUCTION_TIME
                    }

                    accumulatedProgress = (accumulatedProgress + progressIncrement).coerceAtMost(1f)

                    // 큐의 첫 번째 항목 업데이트
                    nexus.updateQueue(nexus.productionQueue.value.toMutableList().apply {
                        if (isNotEmpty()) {
                            this[0] = this[0].copy(
                                progress = accumulatedProgress,
                            )
                        }
                    })

                    nexus.updateProductionState(ProductionState.Producing(accumulatedProgress))
                    delay(16) // 약 60fps
                }

                nexus.incrementProbeCount()
                nexus.updateQueue(nexus.productionQueue.value.drop(1))
                nexus.updateProductionState(ProductionState.Complete)
                delay(500)

                if (nexus.productionQueue.value.isEmpty()) {
                    nexus.updateProductionState(ProductionState.Idle)
                }
            }
            nexus.updateProductionState(ProductionState.Idle)
        }
    }

    fun applyChronoBoost(nexus: Nexus) {
        if (nexus.chronoBoostState.value.isActive) return

        nexus.chronoBoostJob?.cancel()
        nexus.chronoBoostJob = scope.launch {
            nexus.updateChronoBoostState(
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
                    nexus.updateChronoBoostState(ChronoBoostState())
                    break
                }

                nexus.updateChronoBoostState(
                    nexus.chronoBoostState.value.copy(
                        remainingTimeMillis = remainingTime
                    )
                )
            }
        }
    }

    fun cancelProduction(
        nexus: Nexus,
        index: Int
    ) {
        if (index < 0 || index >= nexus.productionQueue.value.size) return

        val newQueue = nexus.productionQueue.value.toMutableList()
        newQueue.removeAt(index)
        nexus.updateQueue(newQueue)

        // 현재 생산 중인 항목을 취소한 경우
        if (index == 0) {
            nexus.productionJob?.cancel()
            nexus.updateProductionState(ProductionState.Idle)

            // 남은 대기열이 있다면 다시 시작
            if (newQueue.isNotEmpty()) {
                startProductionProcess(nexus)
            }
        }
    }

}
