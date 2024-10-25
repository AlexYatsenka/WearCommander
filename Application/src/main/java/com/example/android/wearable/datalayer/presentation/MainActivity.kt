package com.example.android.wearable.datalayer.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android.wearable.datalayer.di.DaggerAppComponent
import com.example.android.wearable.datalayer.presentation.main.MainScreen
import com.example.android.wearable.datalayer.presentation.newS.NewScreen
import com.google.android.gms.wearable.Wearable
import javax.inject.Inject

@SuppressLint("VisibleForTests")
class MainActivity : ComponentActivity() {

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerAppComponent.factory().create(this).inject(this)
    }

    @Inject
    fun init(mainViewModelFactory : MainViewModel.Factory) {
        val mainViewModel = mainViewModelFactory.create(
            capabilityClient,
            messageClient,
            dataClient
        )
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val items by mainViewModel.items.collectAsStateWithLifecycle(emptyList())
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            items = items,
                            onNewScreen = { navController.navigate("new") }
                        )
                    }
                    composable("new") {
                        NewScreen(
                            onNewCommand = { mainViewModel.addNewItem(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val START_ACTIVITY_PATH = "/start-activity"
        const val WEAR_CAPABILITY = "wear"
    }
}
