package com.brs.basqueteapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.brs.basqueteapp.data.GameRepository
import com.brs.basqueteapp.model.GameState
import com.brs.basqueteapp.model.Player
import com.brs.basqueteapp.model.StatEvent
import com.brs.basqueteapp.model.StatType
import com.brs.basqueteapp.model.Team
import java.util.UUID

enum class Screen { SETUP, TRACK, BOX }

class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = GameRepository(app.applicationContext)

    var state by mutableStateOf(repo.load())
        private set

    var screen by mutableStateOf(if (state.started) Screen.TRACK else Screen.SETUP)
        private set

    var selectedPlayerId by mutableStateOf<String?>(null)
        private set

    private fun update(newState: GameState) {
        state = newState
        repo.save(newState)
    }

    fun goTo(target: Screen) { screen = target }

    fun renameTeam(teamId: String, name: String) {
        update(
            when (teamId) {
                state.teamA.id -> state.copy(teamA = state.teamA.copy(name = name))
                state.teamB.id -> state.copy(teamB = state.teamB.copy(name = name))
                else -> state
            }
        )
    }

    fun addPlayer(teamId: String, number: String, name: String) {
        val player = Player(
            id = UUID.randomUUID().toString(),
            number = number.trim(),
            name = name.trim()
        )
        update(
            when (teamId) {
                state.teamA.id -> state.copy(teamA = state.teamA.copy(players = state.teamA.players + player))
                state.teamB.id -> state.copy(teamB = state.teamB.copy(players = state.teamB.players + player))
                else -> state
            }
        )
    }

    fun removePlayer(teamId: String, playerId: String) {
        if (selectedPlayerId == playerId) selectedPlayerId = null
        update(
            when (teamId) {
                state.teamA.id -> state.copy(teamA = state.teamA.copy(players = state.teamA.players.filterNot { it.id == playerId }))
                state.teamB.id -> state.copy(teamB = state.teamB.copy(players = state.teamB.players.filterNot { it.id == playerId }))
                else -> state
            }
        )
    }

    fun startGame() {
        update(state.copy(started = true))
        screen = Screen.TRACK
    }

    fun select(playerId: String) {
        selectedPlayerId = if (selectedPlayerId == playerId) null else playerId
    }

    fun teamIdOf(playerId: String): String? {
        return when {
            state.teamA.players.any { it.id == playerId } -> state.teamA.id
            state.teamB.players.any { it.id == playerId } -> state.teamB.id
            else -> null
        }
    }

    /** Registra uma estatística para o jogador atualmente selecionado. */
    fun record(stat: StatType): Boolean {
        val pid = selectedPlayerId ?: return false
        val tid = teamIdOf(pid) ?: return false
        val event = StatEvent(
            id = UUID.randomUUID().toString(),
            teamId = tid,
            playerId = pid,
            stat = stat,
            timestamp = System.currentTimeMillis()
        )
        update(state.copy(events = state.events + event))
        return true
    }

    fun undo(): StatEvent? {
        val last = state.events.lastOrNull() ?: return null
        update(state.copy(events = state.events.dropLast(1)))
        return last
    }

    /** Zera apenas os eventos, mantendo os elencos cadastrados. */
    fun resetStats() {
        selectedPlayerId = null
        update(state.copy(events = emptyList()))
    }

    /** Recomeça do zero: apaga times, jogadores e eventos. */
    fun newGame() {
        selectedPlayerId = null
        update(GameState())
        screen = Screen.SETUP
    }
}
