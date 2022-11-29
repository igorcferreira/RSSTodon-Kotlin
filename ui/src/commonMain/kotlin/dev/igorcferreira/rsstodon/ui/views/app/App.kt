package dev.igorcferreira.rsstodon.ui.views.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline

@Composable
fun App(
    client: MastodonClient,
    appPipeline: AppPipeline
) = App(AppCoordinator(client, appPipeline))

@Composable
fun App(
    viewModel: AppCoordinator
) {
    val state = remember { viewModel.start() }
    state.forEach { it() }
}