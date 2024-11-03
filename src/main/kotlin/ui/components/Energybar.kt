package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import ui.theme.ProtossBlue
import ui.theme.ProtossGold

@Composable
fun EnergyBar(
    progress: Float,
    isChronoBoosted: Boolean = false,
    modifier: Modifier = Modifier
) {
    val progressColor = if (isChronoBoosted) {
        Brush.linearGradient(
            colors = listOf(
                ProtossGold.copy(alpha = 0.7f),
                ProtossGold,
                ProtossGold.copy(alpha = 0.7f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                ProtossBlue.copy(alpha = 0.7f),
                ProtossBlue,
                ProtossBlue.copy(alpha = 0.7f)
            )
        )
    }

    Box(
        modifier = modifier
            .height(8.dp)
            .background(MaterialTheme.colors.surface)
            .clip(RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .background(progressColor)
        )
    }
}
