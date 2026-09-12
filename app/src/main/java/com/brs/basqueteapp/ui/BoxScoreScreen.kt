package com.brs.basqueteapp.ui

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brs.basqueteapp.model.GameState
import com.brs.basqueteapp.model.PlayerTotals
import com.brs.basqueteapp.model.Team
import com.brs.basqueteapp.model.teamPoints
import com.brs.basqueteapp.model.totalsFor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScoreScreen(vm: GameViewModel) {
    val state = vm.state
    val context = LocalContext.current
    var confirmReset by remember { mutableStateOf(false) }
    var confirmNew by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Box Score", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = { vm.goTo(Screen.TRACK) }) {
                        Text("‹ Voltar", color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            Text(
                "${state.teamA.name}  ${state.events.teamPoints(state.teamA.id)}" +
                    "  x  " +
                    "${state.events.teamPoints(state.teamB.id)}  ${state.teamB.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { TeamTable(state, state.teamA) }
                item { TeamTable(state, state.teamB) }
                item { Spacer(Modifier.width(1.dp)) }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { context.shareBoxScore(state) },
                    modifier = Modifier.weight(1f)
                ) { Text("Compartilhar") }
                OutlinedButton(
                    onClick = { confirmReset = true },
                    modifier = Modifier.weight(1f)
                ) { Text("Zerar dados") }
                OutlinedButton(
                    onClick = { confirmNew = true },
                    modifier = Modifier.weight(1f)
                ) { Text("Nova partida") }
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Zerar estatísticas?") },
            text = { Text("Apaga todos os pontos e jogadas registrados, mas mantém os jogadores cadastrados.") },
            confirmButton = {
                TextButton(onClick = { vm.resetStats(); confirmReset = false; vm.goTo(Screen.TRACK) }) {
                    Text("Zerar")
                }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancelar") } }
        )
    }

    if (confirmNew) {
        AlertDialog(
            onDismissRequest = { confirmNew = false },
            title = { Text("Nova partida?") },
            text = { Text("Apaga os times, os jogadores e todas as estatísticas. Começa do zero.") },
            confirmButton = {
                TextButton(onClick = { vm.newGame(); confirmNew = false }) { Text("Recomeçar") }
            },
            dismissButton = { TextButton(onClick = { confirmNew = false }) { Text("Cancelar") } }
        )
    }
}

private val wNum: Dp = 34.dp
private val wName: Dp = 120.dp
private val wStat: Dp = 40.dp

@Composable
private fun TeamTable(state: GameState, team: Team) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(vertical = 8.dp)) {
            Text(
                team.name,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
            val scroll = rememberScrollState()
            Column(Modifier.horizontalScroll(scroll)) {
                HeaderRow()
                Divider()
                team.players.forEach { p ->
                    StatRow(p.number, p.name, state.events.totalsFor(p.id))
                }
                Divider()
                // Totais do time
                var tot = PlayerTotals()
                team.players.forEach { p ->
                    val x = state.events.totalsFor(p.id)
                    tot = PlayerTotals(
                        points = tot.points + x.points,
                        ft = tot.ft + x.ft, fg2 = tot.fg2 + x.fg2, fg3 = tot.fg3 + x.fg3,
                        reb = tot.reb + x.reb, ast = tot.ast + x.ast, stl = tot.stl + x.stl,
                        blk = tot.blk + x.blk, to = tot.to + x.to
                    )
                }
                StatRow("", "TOTAL", tot, bold = true)
            }
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Cell("#", wNum, header = true)
        Cell("Jogador", wName, header = true, align = TextAlign.Start)
        Cell("PTS", wStat, header = true)
        Cell("REB", wStat, header = true)
        Cell("AST", wStat, header = true)
        Cell("ROU", wStat, header = true)
        Cell("TOC", wStat, header = true)
        Cell("ERR", wStat, header = true)
    }
}

@Composable
private fun StatRow(number: String, name: String, t: PlayerTotals, bold: Boolean = false) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Cell(number, wNum, bold = bold)
        Cell(name.ifBlank { "(sem nome)" }, wName, align = TextAlign.Start, bold = bold)
        Cell(t.points.toString(), wStat, bold = true)
        Cell(t.reb.toString(), wStat, bold = bold)
        Cell(t.ast.toString(), wStat, bold = bold)
        Cell(t.stl.toString(), wStat, bold = bold)
        Cell(t.blk.toString(), wStat, bold = bold)
        Cell(t.to.toString(), wStat, bold = bold)
    }
}

@Composable
private fun Cell(
    text: String,
    width: Dp,
    header: Boolean = false,
    bold: Boolean = false,
    align: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        textAlign = align,
        fontSize = if (header) 11.sp else 13.sp,
        fontWeight = if (header || bold) FontWeight.Bold else FontWeight.Normal,
        color = if (header) MaterialTheme.colorScheme.onSurfaceVariant
        else MaterialTheme.colorScheme.onSurface,
        maxLines = 1
    )
}

/** Monta um resumo em texto e abre o menu de compartilhamento do Android. */
private fun android.content.Context.shareBoxScore(state: GameState) {
    val sb = StringBuilder()
    val ptsA = state.events.teamPoints(state.teamA.id)
    val ptsB = state.events.teamPoints(state.teamB.id)
    sb.appendLine("${state.teamA.name} $ptsA x $ptsB ${state.teamB.name}")
    sb.appendLine()
    listOf(state.teamA, state.teamB).forEach { team ->
        sb.appendLine(team.name)
        team.players.forEach { p ->
            val t = state.events.totalsFor(p.id)
            val num = if (p.number.isBlank()) "" else "#${p.number} "
            sb.appendLine(
                "$num${p.name.ifBlank { "(sem nome)" }}  " +
                    "PTS ${t.points} | REB ${t.reb} | AST ${t.ast} | ROU ${t.stl} | TOC ${t.blk} | ERR ${t.to}"
            )
        }
        sb.appendLine()
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, sb.toString().trim())
    }
    startActivity(Intent.createChooser(intent, "Compartilhar box score"))
}
