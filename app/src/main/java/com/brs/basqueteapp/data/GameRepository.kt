package com.brs.basqueteapp.data

import android.content.Context
import com.brs.basqueteapp.model.GameState
import kotlinx.serialization.json.Json
import java.io.File

/** Persistência simples da partida em arquivo JSON no armazenamento interno. */
class GameRepository(context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }

    private val file: File = File(context.filesDir, "game.json")

    fun load(): GameState {
        return try {
            if (file.exists()) {
                json.decodeFromString(GameState.serializer(), file.readText())
            } else {
                GameState()
            }
        } catch (e: Exception) {
            GameState()
        }
    }

    fun save(state: GameState) {
        try {
            file.writeText(json.encodeToString(GameState.serializer(), state))
        } catch (e: Exception) {
            // Falha de escrita não deve derrubar o app.
        }
    }
}
