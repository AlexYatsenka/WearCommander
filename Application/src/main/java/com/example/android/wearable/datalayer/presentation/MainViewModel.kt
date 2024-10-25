package com.example.android.wearable.datalayer.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexyatsenka.common.domain.models.Command
import com.alexyatsenka.common.domain.repo.CommandRepo
import com.example.android.wearable.datalayer.BuildConfig
import com.example.android.wearable.datalayer.presentation.MainActivity.Companion.START_ACTIVITY_PATH
import com.example.android.wearable.datalayer.presentation.MainActivity.Companion.WEAR_CAPABILITY
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.AvailabilityException
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.PutDataRequest
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel @AssistedInject constructor(
    private val commandRepo: CommandRepo,
    private val gson : Gson,
    @Assisted private val capabilityClient: CapabilityClient,
    @Assisted private val messageClient: MessageClient,
    @Assisted private val dataClient: DataClient
) : ViewModel() {

    val items = commandRepo.getCommands()

    init {
        viewModelScope.launch {
            items.collect {
                //startWearableActivity()
                delay(5000)
                val payload = gson.toJson(it)
                Log.d("Test", "Send data: $payload")
                sendData(payload.toByteArray())
            }
        }
    }

    fun addNewItem(command : Command) {
        viewModelScope.launch {
            commandRepo.saveCommand(command)
        }
    }

    private fun startWearableActivity() {
        viewModelScope.launch {
            try {
                val nodes = capabilityClient
                    .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                    .await()
                    .nodes

                nodes.map { node ->
                    async {
                        messageClient.sendMessage(node.id, START_ACTIVITY_PATH, byteArrayOf())
                            .await()
                    }
                }.awaitAll()

                Log.d("Test", "Starting activity requests sent successfully")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.d("Test", "Starting activity failed: $exception")
            }
        }
    }

    @SuppressLint("VisibleForTests")
    private fun sendData(byteArray: ByteArray) {
        CoroutineScope(Dispatchers.Default).launch {
            if(isAvailable(capabilityClient)) {
                val request = PutDataRequest.create(BuildConfig.PATH_ITEMS).apply {
                    data = byteArray
                }
                Log.d("Test", "Prepare sent: $request")
                dataClient.putDataItem(request)
                    .addOnSuccessListener {
                        Log.d("Test", "Data sent: $it")
                    }
                    .addOnFailureListener {
                        Log.d("Test", "Data sent fail", it)
                    }
            }

        }

    }

    private suspend fun isAvailable(api: GoogleApi<*>): Boolean {
        return try {
            GoogleApiAvailability.getInstance()
                .checkApiAvailability(api)
                .await()

            true
        } catch (e: AvailabilityException) {
            Log.d(
                "Test",
                "${api.javaClass.simpleName} API is not available in this device."
            )
            false
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            capabilityClient: CapabilityClient,
            messageClient: MessageClient,
            dataClient: DataClient
        ) : MainViewModel
    }
}
