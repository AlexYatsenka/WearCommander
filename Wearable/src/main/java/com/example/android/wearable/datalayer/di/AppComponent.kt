package com.example.android.wearable.datalayer.di

import android.content.Context
import com.alexyatsenka.common.di.CommandModule
import com.example.android.wearable.datalayer.presentation.MainActivity
import com.example.android.wearable.datalayer.presentation.MainViewModel
import com.example.android.wearable.datalayer.presentation.tiles.MainTile
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        CommandModule::class
    ]
)
@Singleton
interface AppComponent {
    fun inject(activity : MainActivity)
    fun inject(tile : MainTile)

    @Singleton
    fun getViewModel() : MainViewModel.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context : Context
        ) : AppComponent
    }
}
