package com.alexyatsenka.common.presentation

import com.alexyatsenka.common.domain.models.Command

data class CommandUi(
    override val id: Int = 0,
    override val title: String,
    override val url: String
) : Command()
