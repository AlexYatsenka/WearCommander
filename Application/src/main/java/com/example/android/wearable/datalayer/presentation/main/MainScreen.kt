package com.example.android.wearable.datalayer.presentation.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexyatsenka.common.domain.models.Command

@Composable
fun MainScreen(
    items : List<Command>,
    onNewScreen : () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row {
            Text(
                text = "Commands",
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Card(
                modifier = Modifier.clickable(onClick = onNewScreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        LazyColumn {
            items(items) {
                Text(text = it.title)
            }
        }
    }
}
