package ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ui.theme.ProtossGold
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RemainingTimeText(
    remainingMillis: Long,
    modifier: Modifier = Modifier
) {
    val seconds = (remainingMillis / 1000.0).roundToInt()
    val deciseconds = ((remainingMillis % 1000) / 100).toInt()

    var previousSeconds by remember { mutableStateOf(seconds) }
    var previousDeciseconds by remember { mutableStateOf(deciseconds) }

    // 숫자가 변경될 때 애니메이션 효과
    LaunchedEffect(seconds, deciseconds) {
        previousSeconds = seconds
        previousDeciseconds = deciseconds
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.surface.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // 초 부분
        AnimatedContent(
            targetState = seconds,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut())
                } else {
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut())
                }
            }
        ) { targetSeconds ->
            Text(
                text = "$targetSeconds",
                style = MaterialTheme.typography.h6.copy(
                    color = ProtossGold,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                )
            )
        }

        Text(
            text = ".",
            style = MaterialTheme.typography.h6.copy(
                color = ProtossGold,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 2.dp)
        )

        // 소수점 부분
        AnimatedContent(
            targetState = deciseconds,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut())
                } else {
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut())
                }
            }
        ) { targetDeciseconds ->
            Text(
                text = "$targetDeciseconds",
                style = MaterialTheme.typography.h6.copy(
                    color = ProtossGold,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                )
            )
        }

        Text(
            text = "초",
            style = MaterialTheme.typography.body1.copy(
                color = ProtossGold.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
