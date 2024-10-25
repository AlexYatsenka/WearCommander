package com.example.android.wearable.datalayer.di

import android.content.Context
import com.alexyatsenka.common.di.CommandModule
import com.example.android.wearable.datalayer.presentation.MainActivity
import com.example.android.wearable.datalayer.presentation.MainViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        CommandModule::class,
        AppModule::class
    ]
)
@Singleton
interface AppComponent {

    fun inject(activity: MainActivity)
    fun getMainViewModel() : MainViewModel.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context : Context
        ) : AppComponent
    }
}
