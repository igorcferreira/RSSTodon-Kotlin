package dev.igorcferreira.rsstodon.ui.views.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.ui.views.MainAppTopBar
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline

@Composable
fun Post(
    client: MastodonClient,
    appPipeline: AppPipeline,
    showBackButton: Boolean = true
) = Post(showBackButton, PostViewModel(client, appPipeline))

@Composable
fun Post(
    showBackButton: Boolean = true,
    postViewModel: PostViewModel
) = Scaffold(
    topBar = { PostTopBar(showBackButton, postViewModel) },
    content = { PostContent(postViewModel) },
    contentColor = MaterialTheme.colorScheme.onBackground,
    backgroundColor = MaterialTheme.colorScheme.background,
    isFloatingActionButtonDocked = true,
    floatingActionButtonPosition = FabPosition.End,
    floatingActionButton = {
        FloatingActionButton(onClick = postViewModel::post) {
            Icon(Icons.Default.Done, contentDescription = "Post")
        }
    }
)

@Composable
fun PostTopBar(
    showBackButton: Boolean,
    postViewModel: PostViewModel
) = MainAppTopBar(title = "New Post", navigationIcon = {
    if (showBackButton) {
        Button(onClick = postViewModel::cancel, colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primaryContainer)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
})

@Composable
fun PostContent(
    postViewModel: PostViewModel
) {
    val error = remember { postViewModel.error }
    Column {
        if (error.value != null) {
            Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.error).padding(8.dp)) {
                Text(error.value?.message ?: "Error", color = MaterialTheme.colorScheme.onError)
            }
        }
        ComposeView(postViewModel)
    }
}


@Composable
fun ComposeView(
    postViewModel: PostViewModel
) {
    val message = remember { postViewModel.message }
    val language = remember { postViewModel.language }
    val showList = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    fun handleSelection(selected: PostViewModel.Language) {
        language.value = selected
        focusManager.clearFocus(true)
    }

    Column(Modifier.verticalScroll(rememberScrollState()).fillMaxSize().padding(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = message.value,
            onValueChange = { postViewModel.message.value = it },
            label = { Text("Toot") }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().onFocusChanged { showList.value = it.isFocused },
            value = language.value.label,
            readOnly = true,
            onValueChange = { language.value = PostViewModel.Language.valueOf(it) },
            label = { Text("Language") }
        )
        if (showList.value) {
            Card(elevation = 2.dp, backgroundColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface) {
                LanguageSelector(PostViewModel.Language.values().toList(), ::handleSelection)
            }
        }
    }

    LaunchedEffect(key1 = postViewModel) {
        postViewModel.connect()
    }
    DisposableEffect(key1 = postViewModel) { onDispose {
        postViewModel.disconnect()
    }}
}

@Composable
fun LanguageSelector(
    options: List<PostViewModel.Language>,
    onSelection: (PostViewModel.Language) -> Unit
) {
    Column {
        options.forEach { language ->
            key(language) {
                Row(Modifier.clickable { onSelection(language) }.fillMaxWidth().padding(8.dp)) {
                    Text(language.label, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}