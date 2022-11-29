@file:OptIn(ExperimentalComposeUiApi::class)

package dev.igorcferreira.rsstodon.app

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import androidx.compose.ui.window.*
import dev.igorcferreira.rsstodon.app.domain.ApplicationCoordinator
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import dev.igorcferreira.rsstodon.ui.views.launcher.Splash
import dev.igorcferreira.rsstodon.ui.views.post.Post
import dev.igorcferreira.rsstodon.ui.views.theme.AppTheme
import org.jsoup.Jsoup

fun main() = application {
    val state = remember { ApplicationCoordinator(::exitApplication) }
    val appPipeline = AppPipeline(
        stringFormatter = { Text(Jsoup.parse(it).text()) },
        urlLauncher = state::open,
        printLogs = BuildConfig.DEBUG
    )

    fun keyEvent(event: KeyEvent): Boolean {
        if (event.isMetaPressed.not()) return false
        if (event.key != Key.DirectionLeft) return false
        appPipeline.trigger(AppPipeline.Event.DISMISS)
        return true
    }

    state.windowState.forEach { type ->
        Window(
            onCloseRequest = { state.dismiss(type) },
            title = "RSStodon",
            onKeyEvent = ::keyEvent
        ) {
            Menu(type, state, appPipeline)
            View(type, state, appPipeline)
        }
    }

}

@Composable
fun View(
    type: ApplicationCoordinator.WindowType,
    state: ApplicationCoordinator,
    appPipeline: AppPipeline
) {
    when(type) {
        ApplicationCoordinator.WindowType.SPLASH -> Splash(
            client = state.client,
            appPipeline = appPipeline
        )
        ApplicationCoordinator.WindowType.POST -> AppTheme {
            Post(
                client = state.client,
                appPipeline = appPipeline
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FrameWindowScope.Menu(
    type: ApplicationCoordinator.WindowType,
    state: ApplicationCoordinator,
    appPipeline: AppPipeline
) {
    MenuBar {
        Menu("File") {
            Item("New Post", shortcut = KeyShortcut(Key.N, meta = true)) {
                appPipeline.trigger(AppPipeline.Event.COMPOSE)
            }
            Separator()
            Item("Close Window", shortcut = KeyShortcut(Key.W, meta = true)) {
                state.dismiss(type)
            }
        }
        Menu("Timeline") {
            Item("Refresh", shortcut = KeyShortcut(Key.R, meta = true)) {
                appPipeline.trigger(AppPipeline.Event.REFRESH)
            }
        }
        Menu("Post") {
            Item("Send", shortcut = KeyShortcut(Key.Enter, meta = true)) {
                appPipeline.trigger(AppPipeline.Event.POST)
            }
            Item("Dismiss", shortcut = KeyShortcut(Key.Escape)) {
                appPipeline.trigger(AppPipeline.Event.DISMISS)
            }
        }
    }
}