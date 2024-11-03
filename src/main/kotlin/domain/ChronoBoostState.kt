package domain

data class ChronoBoostState(
    val isActive: Boolean = false,
    val remainingTimeMillis: Long = 0L
)
