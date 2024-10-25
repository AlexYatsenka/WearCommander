package com.alexyatsenka.common.data

import com.alexyatsenka.common.data.models.CommandDB
import com.alexyatsenka.common.domain.models.Command
import com.alexyatsenka.common.domain.repo.CommandRepo

class CommandRepoImpl(
    private val database: CommandDao
) : CommandRepo {

    override suspend fun saveCommand(command: Command) {
        database.saveCommand(CommandDB(command))
    }

    override suspend fun saveCommands(commands: List<Command>) {
        database.saveCommands(commands.map { CommandDB(it) })
    }

    override fun getCommands() = database.getCommands()
    override suspend fun getCommandsSync() = database.getCommandsSync()
}
