package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import domain.Nexus
import domain.ProductionState
import ui.components.ProductionQueue
import ui.components.ProtossButton
import ui.components.RemainingTimeText
import ui.theme.ProtossGold
import viewmodel.NexusViewModel

@Composable
fun NexusDetailScreen(
    viewModel: NexusViewModel,
    nexus: Nexus,
    onBack: () -> Unit
) {
    val productionState by nexus.productionState.collectAsState()
    val chronoBoostState by nexus.chronoBoostState.collectAsState()
    val probeCount by nexus.probeCount.collectAsState()
    val productionQueue by nexus.productionQueue.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.background,
                        MaterialTheme.colors.background.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        // 뒤로 가기 버튼을 좌측 상단에 배치
        ProtossButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text("뒤로 가기")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Protoss Nexus #${nexus.number}",
                style = MaterialTheme.typography.h4.copy(
                    color = ProtossGold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 프로브 카운트 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.8f),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "프로브",
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "$probeCount",
                        style = MaterialTheme.typography.h6
                    )
                }
            }

            // 생산 진행바
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.8f),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "생산 진행",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    when (productionState) {
                        is ProductionState.Producing -> {
                            val progress = (productionState as ProductionState.Producing).progress
                            val isChronoBoosted = chronoBoostState.isActive
                            LinearProgressIndicator(
                                progress = progress,
                                color = if (isChronoBoosted) ProtossGold else MaterialTheme.colors.primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        ProductionState.Complete,
                        ProductionState.Idle -> {
                            LinearProgressIndicator(
                                progress = 0f,
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                backgroundColor = if (chronoBoostState.isActive)
                    ProtossGold.copy(alpha = 0.1f)
                else
                    MaterialTheme.colors.surface.copy(alpha = 0.8f),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (chronoBoostState.isActive)
                            "시간증폭 활성화"
                        else
                            "시간증폭 대기중",
                        style = MaterialTheme.typography.h6.copy(
                            color = if (chronoBoostState.isActive)
                                ProtossGold
                            else
                                MaterialTheme.colors.onSurface
                        )
                    )

                    if (chronoBoostState.isActive) {
                        RemainingTimeText(
                            remainingMillis = chronoBoostState.remainingTimeMillis,
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }

            // 생산 대기열
            ProductionQueue(
                items = productionQueue,
                onCancelItem = { index ->
                    viewModel.cancelProduction(nexus, index)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // 컨트롤 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProtossButton(
                    onClick = { viewModel.startProbeProduction(nexus) },
                    enabled = productionQueue.size < 5,
                    modifier = Modifier.width(160.dp)
                ) {
                    Text("Prove 생산")
                }

                ProtossButton(
                    onClick = { viewModel.applyChronoBoost(nexus) },
                    enabled = !chronoBoostState.isActive,
                    modifier = Modifier.width(160.dp)
                ) {
                    Text("시간증폭")
                }
            }
        }
    }
}
