package domain

sealed interface ProductionState {
    data object Idle : ProductionState
    data class Producing(val progress: Float) : ProductionState
    data object Complete : ProductionState
}
