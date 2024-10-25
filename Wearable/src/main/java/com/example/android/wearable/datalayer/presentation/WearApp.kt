package com.example.android.wearable.datalayer.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.alexyatsenka.common.domain.models.Command
import com.alexyatsenka.common.presentation.CommandUi
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun WearApp(
    items : List<Command>,
    onCommandClick : (Command) -> Unit
) {
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Card,
            last = ItemType.Card,
        ),
        rotaryMode = ScalingLazyColumnState.RotaryMode.Snap
    )
    ScreenScaffold(scrollState = columnState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = items.isEmpty(), label = ""
            ) {
                if(it) {
                    Text(
                        text = "No data"
                    )
                } else {
                    ScalingLazyColumn(
                        columnState = columnState
                    ) {
                        item {
                            Spacer(modifier = Modifier.fillMaxHeight(0.2188f))
                        }
                        items(items.size) { index ->
                            Card(
                                onClick = {
                                    onCommandClick(items[index])
                                },
                            ) {
                                Text(
                                    text = items[index].title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.fillMaxHeight(0.3646f))
                        }
                    }

                }
            }
            TimeText()
        }
    }
}
