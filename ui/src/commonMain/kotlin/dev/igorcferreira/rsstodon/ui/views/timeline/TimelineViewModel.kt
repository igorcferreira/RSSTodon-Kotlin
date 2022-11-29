package dev.igorcferreira.rsstodon.ui.views.timeline

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.api.model.presentation.StatusContent
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import dev.igorcferreira.rsstodon.ui.views.support.async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import java.util.logging.Logger

class TimelineViewModel(
    private val client: MastodonClient,
    private val appPipeline: AppPipeline
) {
    private lateinit var items: Flow<List<StatusContent>>
    private lateinit var pipelineKey: String
    private val timer = Timer("Post pooling", false)
    val isRefreshing = mutableStateOf(false)
    val error = mutableStateOf<Exception?>(null)
    val listState = LazyListState(0, 0)

    fun fetchItems(scope: CoroutineScope): Flow<List<StatusContent>> {
        if (::items.isInitialized) { return  items }
        items = client.getTimelineFlow()
        loadContent(scope)
        return items
    }

    fun loadContent(scope: CoroutineScope) = async {
        isRefreshing.value = true
        try {
            error.value = null
            client.refreshTimeline()
            scrollToTop(scope)
        } catch (ex: Exception) {
            Logger.getGlobal().log(Level.INFO, "Exception: ${ex.message}")
            error.value = ex
        }
        isRefreshing.value = false
    }

    fun compose() {
        appPipeline.trigger(AppPipeline.Event.COMPOSE)
    }

    @Composable
    fun format(content: String) {
        appPipeline.format(content)
    }

    fun connect(scope: CoroutineScope) {
        pipelineKey = appPipeline.addEventListener { event ->
            if (event == AppPipeline.Event.REFRESH) {
                loadContent(scope)
            }
        }
        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() { async {
                val atTop = listState.firstVisibleItemIndex == 0
                client.refreshTimeline()
                if (atTop && listState.isScrollInProgress.not()) {
                    scrollToTop(scope)
                }
            } }
        }, TimeUnit.MINUTES.toMillis(1), TimeUnit.MINUTES.toMillis(1))
    }

    suspend fun scrollToTop(scope: CoroutineScope) {
        delay(300L)
        scope.launch { listState.animateScrollToItem(0, 0) }
    }

    fun disconnect() {
        timer.apply {
            cancel(); purge()
        }
        if (::pipelineKey.isInitialized.not()) return
        appPipeline.removeEventListener(pipelineKey)
    }
}