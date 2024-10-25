package com.example.android.wearable.datalayer.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class AppModule {

    @Provides
    @Reusable
    fun provideGson() = Gson()
}
