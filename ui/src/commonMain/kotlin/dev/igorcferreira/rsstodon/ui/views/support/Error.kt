package dev.igorcferreira.rsstodon.ui.views.support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Error(
    message: String,
    dismiss: () -> Unit
) = Column(modifier = Modifier.padding(8.dp)) {
    Text("Error")
    Text(message)
    Button(onClick = dismiss) {
        Text("Ok")
    }
}