package com.alexyatsenka.common.domain.repo

import com.alexyatsenka.common.domain.models.Command
import kotlinx.coroutines.flow.Flow

interface CommandRepo {
    suspend fun saveCommand(command : Command)
    suspend fun saveCommands(commands : List<Command>)
    fun getCommands() : Flow<List<Command>>
    suspend fun getCommandsSync() : List<Command>
}
