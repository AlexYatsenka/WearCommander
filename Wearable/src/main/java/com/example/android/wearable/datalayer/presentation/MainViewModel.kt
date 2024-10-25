package com.example.android.wearable.datalayer.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexyatsenka.common.domain.models.Command
import com.alexyatsenka.common.domain.repo.CommandRepo
import com.alexyatsenka.common.presentation.CommandUi
import com.example.android.wearable.datalayer.BuildConfig
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.NodeClient
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel @AssistedInject constructor(
    private val gson : Gson,
    private val commandRepo: CommandRepo,
    @Assisted private val capabilityClient: CapabilityClient,
    @Assisted private val messageClient: MessageClient,
    @Assisted private val nodeClient: NodeClient
) : ViewModel(), DataClient.OnDataChangedListener {

    private val _events = MutableStateFlow<List<Command>>(emptyList())
    val items = _events.asStateFlow()

    init {
        viewModelScope.launch {
            commandRepo.getCommands().collect { commands ->
                _events.update { commands }
            }
        }
    }

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("Test", "Data changed")
        val dataEvent = dataEvents.last()
        when (dataEvent.dataItem.uri.path) {
            BuildConfig.PATH_ITEMS -> {
                val commands = dataEvent.dataItem.data?.let {
                    val source = String(it)
                    Log.d("Test", "Data: $source")
                    gson.fromJson(source, Array<CommandUi>::class.java)
                }
                if(commands != null) {
                    viewModelScope.launch {
                        commandRepo.saveCommands(commands.toList())
                    }
                }
            }
        }
    }

    fun sendCommand(command : Command) {
        Log.d("Test", "Send command")
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Log.d("Test", "Send command failed", throwable)
            }
        ) {
            val nodes = nodeClient.connectedNodes.await()
            Log.d("Test", "Nodes: ${nodes.joinToString { it.displayName }}")
            nodes.map { node ->
                async {
                    val result = messageClient.sendMessage(
                        node.id,
                        BuildConfig.PATH_COMMAND,
                        command.url.toByteArray()
                    ).await()
                    Log.d("Test", "Send command successfully: $result")
                }
            }.awaitAll()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            capabilityClient: CapabilityClient,
            messageClient: MessageClient,
            nodeClient: NodeClient
        ) : MainViewModel
    }
}
