package ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RemainingTimeDisplay(
    remainingMillis: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "남은 시간",
            style = MaterialTheme.typography.subtitle2.copy(
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        )
        RemainingTimeText(
            remainingMillis = remainingMillis
        )
    }
}
