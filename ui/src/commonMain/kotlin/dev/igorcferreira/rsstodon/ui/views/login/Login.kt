package dev.igorcferreira.rsstodon.ui.views.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun Login(
    authorize: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp).background(MaterialTheme.colorScheme.background)) {
        Text(text = "Login on Mastodon", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Button(onClick = authorize) { Text("Login") }
    }
}