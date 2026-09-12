package com.brs.basqueteapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.brs.basqueteapp.model.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(vm: GameViewModel) {
    val state = vm.state
    val canStart = state.teamA.players.isNotEmpty() || state.teamB.players.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BasqueteApp", fontWeight = FontWeight.Bold) },
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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.width(1.dp)) }
                item { TeamCard(vm, state.teamA) }
                item { TeamCard(vm, state.teamB) }
                item {
                    Text(
                        "Cadastre número e nome dos jogadores. Durante a partida, toque num jogador e depois no botão da jogada.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(4.dp)
                    )
                }
                item { Spacer(Modifier.width(1.dp)) }
            }

            Button(
                onClick = { vm.startGame() },
                enabled = canStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text("Iniciar partida", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TeamCard(vm: GameViewModel, team: Team) {
    var number by remember(team.id) { mutableStateOf("") }
    var name by remember(team.id) { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = team.name,
                onValueChange = { vm.renameTeam(team.id, it) },
                label = { Text("Nome do time") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.width(1.dp))

            if (team.players.isEmpty()) {
                Text(
                    "Nenhum jogador ainda.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(Modifier.padding(vertical = 4.dp)) {
                    team.players.forEach { p ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier.width(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (p.number.isBlank()) "-" else "#${p.number}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                p.name.ifBlank { "(sem nome)" },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { vm.removePlayer(team.id, p.id) }) {
                                Icon(Icons.Filled.Close, contentDescription = "Remover")
                            }
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { if (it.length <= 3) number = it.filter { c -> c.isDigit() } },
                    label = { Text("Nº") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(90.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = {
                    if (number.isNotBlank() || name.isNotBlank()) {
                        vm.addPlayer(team.id, number, name)
                        number = ""
                        name = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Adicionar jogador")
            }
        }
    }
}
