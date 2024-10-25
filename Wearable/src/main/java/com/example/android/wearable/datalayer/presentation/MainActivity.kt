package com.example.android.wearable.datalayer.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.widget.ConfirmationOverlay
import com.example.android.wearable.datalayer.di.Dagger
import com.google.android.gms.wearable.Wearable
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    private val dataClient by lazy { Wearable.getDataClient(this) }

    @Inject lateinit var factoryMainViewModel : MainViewModel.Factory
    private val mainViewModel by lazy {
        factoryMainViewModel.create(
            Wearable.getCapabilityClient(this),
            Wearable.getMessageClient(this),
            Wearable.getNodeClient(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dagger.buildAppComponent(this).inject(this)
        setContent {
            val items by mainViewModel.items.collectAsStateWithLifecycle()
            WearApp(
                items = items,
                onCommandClick = {
                    mainViewModel.sendCommand(it)
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.SUCCESS_ANIMATION)
                        .setMessage("Command send")
                        .showOn(this)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(mainViewModel)
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(mainViewModel)
    }

    companion object {
        const val MOBILE_CAPABILITY = "mobile"
    }
}
