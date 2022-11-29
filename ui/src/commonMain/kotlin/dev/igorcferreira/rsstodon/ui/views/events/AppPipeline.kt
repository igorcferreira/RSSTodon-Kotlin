package dev.igorcferreira.rsstodon.ui.views.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import dev.igorcferreira.rsstodon.ui.views.support.async
import java.net.URI
import java.util.*

class AppPipeline(
    private val stringFormatter: @Composable (String) -> Unit,
    private val urlLauncher: suspend (uri: URI, response: (String) -> Unit) -> Unit,
    private val printLogs: Boolean = false
) {
    enum class Event {
        REFRESH,
        DISMISS,
        COMPOSE,
        POST,
        CLOSE
    }

    private val eventListeners = mutableStateMapOf<String, suspend (Event) -> Unit>()

    fun addEventListener(listener: suspend (Event) -> Unit): String {
        val call: StackTraceElement = Thread.currentThread().stackTrace[2]
        val key = UUID.randomUUID().toString()
        log("Adding listener $key at $call")
        eventListeners[key] = listener
        return key
    }

    fun removeEventListener(key: String) {
        val call: StackTraceElement = Thread.currentThread().stackTrace[2]
        log("Removing listener $key at $call")
        eventListeners.remove(key)
    }

    fun trigger(event: Event) {
        val call: StackTraceElement = Thread.currentThread().stackTrace[2]
        eventListeners.forEach { async {
            log("Triggering $event at $call on ${it.key}")
            it.value(event)
        } }
    }

    @Composable
    fun format(string: String) {
        stringFormatter(string)
    }

    suspend fun launch(uri: URI, response: (String) -> Unit) = urlLauncher(uri, response)

    private fun log(message: String) {
        if (printLogs) {
            println(message)
        }
    }
}