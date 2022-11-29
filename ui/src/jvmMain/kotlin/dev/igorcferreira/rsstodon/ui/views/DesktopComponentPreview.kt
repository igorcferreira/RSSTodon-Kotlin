package dev.igorcferreira.rsstodon.ui.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import dev.igorcferreira.rsstodon.api.model.presentation.StatusContent
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import dev.igorcferreira.rsstodon.ui.views.launcher.Splash
import dev.igorcferreira.rsstodon.ui.views.post.Post
import dev.igorcferreira.rsstodon.ui.views.preview.MockedMastodonClient
import dev.igorcferreira.rsstodon.ui.views.timeline.ErrorView
import dev.igorcferreira.rsstodon.ui.views.timeline.Timeline
import dev.igorcferreira.rsstodon.ui.views.timeline.TimelineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Preview
@Composable
internal fun Splash_DesktopPreview() = Splash(
    client = MockedMastodonClient(),
    appPipeline = AppPipeline(
        stringFormatter = { Text(it) },
        urlLauncher = { _, _ -> }
    )
)

@Preview
@Composable
internal fun Post_DesktopPreview() = Post(
    client = MockedMastodonClient(),
    appPipeline = AppPipeline(
        stringFormatter = { Text(it) },
        urlLauncher = { _, _ -> }
    )
)

@Preview
@Composable
internal fun Timeline_DesktopPreview() = Timeline(
    client = MockedMastodonClient(),
    appPipeline = AppPipeline(
        stringFormatter = { Text(it) },
        urlLauncher = { _, _ -> }
    )
)


@Preview
@Composable
internal fun Timeline_Empty_ErrorPreview() = ErrorView(
    message = "Empty error",
    content = emptyList(),
    viewModel = TimelineViewModel(MockedMastodonClient(), AppPipeline({ Text(it) }, { _, _ -> })),
    timelineScope = CoroutineScope(Dispatchers.IO)
)

@Preview
@Composable
internal fun Timeline_ErrorPreview() = ErrorView(
    message = "Empty error",
    content = listOf(StatusContent("1", "Mocked", "https://example.com", "Mocked Status 1"), StatusContent("2", "Mocked", "https://example.com", "Mocked Status 2")),
    viewModel = TimelineViewModel(MockedMastodonClient(), AppPipeline({ Text(it) }, { _, _ -> })),
    timelineScope = CoroutineScope(Dispatchers.IO)
)