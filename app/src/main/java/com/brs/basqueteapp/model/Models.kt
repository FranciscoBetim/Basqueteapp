package com.brs.basqueteapp.model

import kotlinx.serialization.Serializable

/** Tipos de estatística registráveis durante a partida. */
enum class StatType(val label: String, val short: String, val points: Int) {
    FT("Lance livre", "LL", 1),
    FG2("2 pontos", "2PT", 2),
    FG3("3 pontos", "3PT", 3),
    REB("Rebote", "REB", 0),
    AST("Assistência", "AST", 0),
    STL("Roubo", "ROU", 0),
    BLK("Toco", "TOC", 0),
    TO("Turnover", "ERR", 0)
}

@Serializable
data class Player(
    val id: String,
    val number: String,
    val name: String
)

@Serializable
data class Team(
    val id: String,
    val name: String,
    val players: List<Player> = emptyList()
)

/** Um evento registrado (usado para totais e para desfazer). */
@Serializable
data class StatEvent(
    val id: String,
    val teamId: String,
    val playerId: String,
    val stat: StatType,
    val timestamp: Long
)

@Serializable
data class GameState(
    val teamA: Team = Team(id = "A", name = "Time A"),
    val teamB: Team = Team(id = "B", name = "Time B"),
    val events: List<StatEvent> = emptyList(),
    val started: Boolean = false
)

/** Totais calculados de um jogador a partir da lista de eventos. */
data class PlayerTotals(
    val points: Int = 0,
    val ft: Int = 0,
    val fg2: Int = 0,
    val fg3: Int = 0,
    val reb: Int = 0,
    val ast: Int = 0,
    val stl: Int = 0,
    val blk: Int = 0,
    val to: Int = 0
)

fun List<StatEvent>.totalsFor(playerId: String): PlayerTotals {
    var p = 0; var ft = 0; var fg2 = 0; var fg3 = 0
    var reb = 0; var ast = 0; var stl = 0; var blk = 0; var to = 0
    for (e in this) {
        if (e.playerId != playerId) continue
        when (e.stat) {
            StatType.FT -> { ft++; p += 1 }
            StatType.FG2 -> { fg2++; p += 2 }
            StatType.FG3 -> { fg3++; p += 3 }
            StatType.REB -> reb++
            StatType.AST -> ast++
            StatType.STL -> stl++
            StatType.BLK -> blk++
            StatType.TO -> to++
        }
    }
    return PlayerTotals(p, ft, fg2, fg3, reb, ast, stl, blk, to)
}

fun List<StatEvent>.teamPoints(teamId: String): Int =
    filter { it.teamId == teamId }.sumOf { it.stat.points }
