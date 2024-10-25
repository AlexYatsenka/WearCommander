package com.example.android.wearable.datalayer.presentation.tiles

import android.annotation.SuppressLint
import android.util.Log
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ActionBuilders.LoadAction
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.Image
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.LayoutElementBuilders.Spacer
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.AndroidImageResourceByResId
import androidx.wear.protolayout.ResourceBuilders.ImageResource
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.StateBuilders
import androidx.wear.protolayout.StateBuilders.State
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TimelineBuilders.TimelineEntry
import androidx.wear.protolayout.TypeBuilders.FloatProp
import androidx.wear.protolayout.expression.AnimationParameterBuilders
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.protolayout.expression.ProtoLayoutExperimental
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.Chip
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.CircularProgressIndicator
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.ProgressIndicatorColors
import androidx.wear.protolayout.material.TitleChip
import androidx.wear.protolayout.material.layouts.MultiButtonLayout
import androidx.wear.protolayout.material.layouts.MultiSlotLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import androidx.wear.widget.ConfirmationOverlay
import com.alexyatsenka.common.domain.models.Command
import com.alexyatsenka.common.domain.repo.CommandRepo
import com.example.android.wearable.datalayer.BuildConfig
import com.example.android.wearable.datalayer.R
import com.example.android.wearable.datalayer.di.Dagger
import com.google.android.gms.wearable.Wearable
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalHorologistApi::class)
class MainTile : SuspendingTileService() {

    @Inject lateinit var commandRepo: CommandRepo
    @Inject lateinit var gson : Gson

    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val nodeClient by lazy { Wearable.getNodeClient(this) }
    private val colors = Colors(
        0xffaac7ff.toInt(),
        0xff0a305f.toInt(),
        0xff111318.toInt(),
        0xffe2e2e9.toInt()
    )

    override fun onCreate() {
        super.onCreate()
        Dagger.buildAppComponent(this)
            .inject(this)
    }

    @androidx.annotation.OptIn(ProtoLayoutExperimental::class)
    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): Resources {
        return Resources.Builder()
            .setVersion("1")
            .addIdToImageMapping(
                "ic_done",
                ImageResource.Builder()
                    .setAndroidResourceByResId(
                        AndroidImageResourceByResId.Builder()
                            .setResourceId(R.drawable.ic_done)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    @SuppressLint("RestrictedApi")
    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): Tile {
        processClick(requestParams.currentState.lastClickableId)
        return Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(
                Timeline.Builder()
                    .addTimelineEntry(
                        TimelineEntry.Builder()
                            .setLayout(
                                Layout.Builder()
                                    .setRoot(
                                        getLayout(
                                            requestParams = requestParams
                                        )
                                    ).build()
                            ).build()
                    ).build()
            ).build()
    }

    private suspend fun processClick(lastClickId : String) {
        if(lastClickId.isNotEmpty()) {
            getCommands().find { it.id == lastClickId.toIntOrNull() }?.let { command ->
                nodeClient.connectedNodes.await().forEach { node ->
                    Log.d("Test", "Send to ${node.displayName}, data: ${command.url}")
                    val result = messageClient.sendMessage(
                        node.id,
                        BuildConfig.PATH_COMMAND,
                        command.url.toByteArray()
                    ).await()
                    Log.d("Test", "Message send with result: $result")
                    getUpdater(this).requestUpdate(MainTile::class.java)
                }
            }
        }
    }

    private suspend fun getLayout(
        size : Int = 2,
        requestParams: RequestBuilders.TileRequest
    ) = Column.Builder().run {
        getCommands().take(size).forEachIndexed { index, command ->
            addContent(getChip(command.id, command.title, requestParams))
            if(index + 1 != size) {
                addContent(
                    Spacer.Builder()
                        .setHeight(dp(4f))
                        .build()
                )
            }
        }
        build()
    }

    @androidx.annotation.OptIn(ProtoLayoutExperimental::class)
    private fun getChip(
        id : Int,
        title : String,
        requestParams: RequestBuilders.TileRequest
    ) = Chip.Builder(
            this@MainTile,
            Clickable.Builder()
                .setId(id.toString())
                .setOnClick(LoadAction.Builder().build())
                .build(),
            requestParams.deviceConfiguration
        ).apply {
            setChipColors(ChipColors.secondaryChipColors(colors))
            if(requestParams.currentState.lastClickableId == id.toString()) {
                setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                setCustomContent(
                    Image.Builder()
                        .setResourceId("ic_done")
                        .setWidth(dp(65f))
                        .setHeight(dp(65f))
                        .setModifiers(
                            ModifiersBuilders.Modifiers.Builder()
                                .setContentUpdateAnimation(
                                    ModifiersBuilders.AnimatedVisibility.Builder()
                                    .setEnterTransition(
                                        ModifiersBuilders.DefaultContentTransitions.fadeIn()
                                    ).setExitTransition(
                                        ModifiersBuilders.DefaultContentTransitions.fadeOut()
                                    ).build()
                                ).build()
                        )
                        .build()
                )
            } else {
                setPrimaryLabelContent(title)
            }
        }.build()

    private val commands = arrayListOf<Command>()
    private suspend fun getCommands() : List<Command> {
        if(commands.isEmpty()) commands.addAll(commandRepo.getCommandsSync())
        return commands
    }
}
