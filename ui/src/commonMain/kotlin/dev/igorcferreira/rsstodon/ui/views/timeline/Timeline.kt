package dev.igorcferreira.rsstodon.ui.views.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.api.model.presentation.StatusContent
import dev.igorcferreira.rsstodon.ui.views.MainAppTopBar
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import kotlinx.coroutines.CoroutineScope

@Composable
fun Timeline(
    client: MastodonClient,
    appPipeline: AppPipeline
) = Timeline(TimelineViewModel(client, appPipeline))

@Composable
fun Timeline(
    viewModel: TimelineViewModel
) {
    val timelineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { MainAppTopBar() },
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = { FloatingAction(viewModel::compose) },
        content = { Content(viewModel, timelineScope) }
    )
    LaunchedEffect(key1 = viewModel) { viewModel.connect(timelineScope) }

    DisposableEffect(key1 = timelineScope) { onDispose {
        viewModel.disconnect()
    } }
}

@Composable
fun FloatingAction(
    postAction: () -> Unit
) {
    FloatingActionButton(onClick = postAction) {
        Icon(Icons.Default.Add, contentDescription = "Post")
    }
}

sealed class TimelineState {
    class Loaded(val items: List<StatusContent>): TimelineState()
    class Error(val message: String, val items: List<StatusContent>): TimelineState()
    class Refreshing(val items: List<StatusContent>): TimelineState()
    object InitialState: TimelineState()
}

@Composable
fun Content(
    viewModel: TimelineViewModel,
    timelineScope: CoroutineScope
) {
    val content = viewModel.fetchItems(timelineScope).collectAsState(emptyList())
    val state = remember(viewModel) { derivedStateOf {
        if (viewModel.error.value != null)
            TimelineState.Error(viewModel.error.value?.message ?: "", content.value)
        else if (content.value.isEmpty())
            TimelineState.InitialState
        else if (viewModel.isRefreshing.value)
            TimelineState.Refreshing(content.value)
        else
            TimelineState.Loaded(content.value)
    } }

    when(val currentState = state.value) {
        is TimelineState.Refreshing -> StatusList(viewModel, currentState.items)
        is TimelineState.Loaded -> StatusList(viewModel, currentState.items)
        is TimelineState.Error -> ErrorView(currentState.message, currentState.items, viewModel, timelineScope)
        is TimelineState.InitialState -> LoadingIndicator(modifier = Modifier.fillMaxSize().padding(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusList(
    viewModel: TimelineViewModel,
    content: List<StatusContent>
) {
    val listState = rememberSaveable(saver = LazyListState.Saver) { viewModel.listState }

    Column(modifier = Modifier.fillMaxSize()) {
        if (viewModel.isRefreshing.value) {
            Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        LazyColumn(state = listState) {
            items(content, key = { it.id }) { status ->
                Card(modifier = Modifier.padding(8.dp)) {
                    StatusItem(status, viewModel)
                }
            }
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    content: List<StatusContent>,
    viewModel: TimelineViewModel,
    timelineScope: CoroutineScope
) {
    if (content.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Error: $message", color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.loadContent(timelineScope) },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Reload", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.errorContainer).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Error: $message", color = MaterialTheme.colorScheme.onErrorContainer)
                Button(
                    onClick = { viewModel.loadContent(timelineScope) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reload", color = MaterialTheme.colorScheme.onError)
                }
            }
            StatusList(viewModel, content)
        }
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        CircularProgressIndicator(color = color)
    }
}

@Composable
fun StatusItem(
    status: StatusContent,
    viewModel: TimelineViewModel
) = Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
    Text(text = status.account, fontWeight = FontWeight.Bold)
    viewModel.format(status.uri)
    Spacer(Modifier.height(8.dp))
    viewModel.format(status.content)
}