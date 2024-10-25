package com.alexyatsenka.common.di

import android.content.Context
import androidx.room.Room
import com.alexyatsenka.common.data.AppDatabase
import com.alexyatsenka.common.data.CommandDao
import com.alexyatsenka.common.data.CommandRepoImpl
import com.alexyatsenka.common.domain.repo.CommandRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CommandModule {

    @Provides
    fun provideCommandDao(context : Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "database"
    ).build().commandDao()

    @Provides
    fun provideCommandRepo(
        context : CommandDao
    ) : CommandRepo = CommandRepoImpl(context)
}
