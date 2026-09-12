package com.brs.basqueteapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brs.basqueteapp.model.Player
import com.brs.basqueteapp.model.StatType
import com.brs.basqueteapp.model.Team
import com.brs.basqueteapp.model.teamPoints
import com.brs.basqueteapp.model.totalsFor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(vm: GameViewModel) {
    val state = vm.state
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val ptsA = state.events.teamPoints(state.teamA.id)
    val ptsB = state.events.teamPoints(state.teamB.id)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ScoreLabel(state.teamA.name, ptsA)
                        Text(
                            "x",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        ScoreLabel(state.teamB.name, ptsB)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            StatBar(
                enabled = vm.selectedPlayerId != null,
                onStat = { stat ->
                    if (!vm.record(stat)) {
                        scope.launch { snackbar.showSnackbar("Toque num jogador primeiro") }
                    }
                },
                onUndo = {
                    val e = vm.undo()
                    scope.launch {
                        snackbar.showSnackbar(
                            if (e == null) "Nada para desfazer"
                            else "Desfeito: ${e.stat.short}"
                        )
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = { vm.goTo(Screen.BOX) }) { Text("Ver box score") }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { vm.goTo(Screen.SETUP) }) { Text("Elenco") }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                teamSection(vm, state.teamA)
                teamSection(vm, state.teamB)
            }
        }
    }
}

private fun LazyListScope.teamSection(
    vm: GameViewModel,
    team: Team
) {
    if (team.players.isEmpty()) return
    item(key = "hdr_${team.id}") {
        Text(
            team.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
        )
    }
    items(team.players, key = { it.id }) { player ->
        PlayerRow(
            vm = vm,
            player = player,
            selected = vm.selectedPlayerId == player.id
        )
    }
}

@Composable
private fun ScoreLabel(name: String, points: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            name.take(10),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 12.sp,
            maxLines = 1
        )
        Text(
            points.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
    }
}

@Composable
private fun PlayerRow(vm: GameViewModel, player: Player, selected: Boolean) {
    val t = vm.state.events.totalsFor(player.id)
    val container = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surface
    val border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null

    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        border = border,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { vm.select(player.id) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (player.number.isBlank()) "-" else player.number,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    player.name.ifBlank { "(sem nome)" },
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    "REB ${t.reb}  •  AST ${t.ast}  •  ROU ${t.stl}  •  TOC ${t.blk}  •  ERR ${t.to}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(Modifier.width(6.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    t.points.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("PTS", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun StatBar(
    enabled: Boolean,
    onStat: (StatType) -> Unit,
    onUndo: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!enabled) {
                Text(
                    "Toque num jogador para marcar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatButton("LL\n+1", enabled, Modifier.weight(1f)) { onStat(StatType.FT) }
                StatButton("+2", enabled, Modifier.weight(1f)) { onStat(StatType.FG2) }
                StatButton("+3", enabled, Modifier.weight(1f)) { onStat(StatType.FG3) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatButton("REB", enabled, Modifier.weight(1f)) { onStat(StatType.REB) }
                StatButton("AST", enabled, Modifier.weight(1f)) { onStat(StatType.AST) }
                StatButton("ROUBO", enabled, Modifier.weight(1f)) { onStat(StatType.STL) }
                StatButton("TOCO", enabled, Modifier.weight(1f)) { onStat(StatType.BLK) }
                StatButton("ERRO", enabled, Modifier.weight(1f)) { onStat(StatType.TO) }
            }
            Button(
                onClick = onUndo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("↶  Desfazer última", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun StatButton(
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp, horizontal = 2.dp),
        modifier = modifier.height(52.dp)
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 14.sp
        )
    }
}
