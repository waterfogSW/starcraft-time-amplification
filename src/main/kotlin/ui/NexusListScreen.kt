package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
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
import ui.components.ProtossButton
import ui.theme.ProtossGold
import viewmodel.NexusViewModel

@Composable
fun NexusListScreen(
    viewModel: NexusViewModel,
    onNexusSelected: (Nexus) -> Unit
) {
    val nexusList by viewModel.nexusList.collectAsState()

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
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 넥서스 목록을 그리드 형태로 표시
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(nexusList.size) { index ->
                    NexusCard(
                        nexus = nexusList[index],
                        onClick = { onNexusSelected(nexusList[index]) }
                    )
                }

                item {
                    ProtossButton(
                        onClick = { viewModel.addNexus() },
                        modifier = Modifier.size(120.dp)
                    ) {
                        Text("+", style = MaterialTheme.typography.h4)
                    }
                }
            }
        }
    }
}

@Composable
private fun NexusCard(
    nexus: Nexus,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable { onClick() },
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.8f),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nexus #${nexus.number}",
                style = MaterialTheme.typography.h6,
                color = ProtossGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Prove: ${nexus.probeCount.collectAsState().value}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
} 
