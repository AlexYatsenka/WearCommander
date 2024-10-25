package com.example.android.wearable.datalayer.di

import android.content.Context


object Dagger {

    private var mAppComponent : AppComponent? = null
    val appComponent get() = mAppComponent!!

    @JvmStatic
    fun buildAppComponent(context : Context) : AppComponent {
        if(mAppComponent == null) {
            synchronized(this) {
                if(mAppComponent == null) {
                    mAppComponent = DaggerAppComponent.factory()
                        .create(context)
                }
            }
        }
        return appComponent
    }
}
