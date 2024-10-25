package com.alexyatsenka.common.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexyatsenka.common.domain.models.Command

@Entity
data class CommandDB(
    @PrimaryKey(autoGenerate = true) override val id : Int = 0,
    override val title: String,
    override val url: String
) : Command() {
    constructor(command: Command) : this(command.id, command.title, command.url)
}
