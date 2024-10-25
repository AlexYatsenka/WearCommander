package com.alexyatsenka.common.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexyatsenka.common.data.models.CommandDB
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCommand(commands : CommandDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCommands(commands : List<CommandDB>)

    @Query("SELECT * FROM CommandDB")
    fun getCommands() : Flow<List<CommandDB>>

    @Query("SELECT * FROM CommandDB")
    suspend fun getCommandsSync() : List<CommandDB>
}
