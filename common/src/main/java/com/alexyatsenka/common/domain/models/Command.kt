package com.alexyatsenka.common.domain.models

abstract class Command {
    abstract val id : Int
    abstract val title: String
    abstract val url: String
}
