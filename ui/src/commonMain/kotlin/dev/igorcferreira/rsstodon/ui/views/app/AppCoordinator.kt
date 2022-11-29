package dev.igorcferreira.rsstodon.ui.views.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import dev.igorcferreira.rsstodon.ui.views.post.Post
import dev.igorcferreira.rsstodon.ui.views.timeline.Timeline

class AppCoordinator(
    val client: MastodonClient,
    private val appPipeline: AppPipeline
) {
    private val pipelineKey: String
    private lateinit var stackState: SnapshotStateList<@Composable () -> Unit>

    init {
        pipelineKey = appPipeline.addEventListener(::handle)
    }

    fun start(): SnapshotStateList<@Composable () -> Unit> {
        if (::stackState.isInitialized) return stackState
        stackState = mutableStateListOf<@Composable () -> Unit>()
        stackState.add { Timeline(client, appPipeline) }
        return stackState
    }

    protected fun finalize() {
        appPipeline.removeEventListener(pipelineKey)
    }

    private fun compose() {
        if (::stackState.isInitialized.not()) return
        stackState.add { Post(client, appPipeline) }
    }

    private fun dismiss() {
        if (::stackState.isInitialized.not()) return
        if (stackState.count() == 1) {
            appPipeline.trigger(AppPipeline.Event.CLOSE)
        } else {
            @Suppress("UNUSED_VARIABLE")
            val item = stackState.removeLastOrNull()
        }
    }

    private fun handle(event: AppPipeline.Event) {
        when (event) {
            AppPipeline.Event.COMPOSE -> compose()
            AppPipeline.Event.DISMISS -> dismiss()
            else -> Unit
        }
    }
}