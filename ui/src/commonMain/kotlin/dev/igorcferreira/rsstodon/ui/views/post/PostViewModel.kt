package dev.igorcferreira.rsstodon.ui.views.post

import androidx.compose.runtime.mutableStateOf
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import dev.igorcferreira.rsstodon.ui.views.support.async

class PostViewModel(
    private val client: MastodonClient,
    private val appPipeline: AppPipeline,
    defaultLanguage: Language = Language.PORTUGUESE
) {
    enum class Language(
        val label: String,
        val code: String
    ) {
        ENGLISH("English", "en"),
        SPANISH("Español", "es"),
        PORTUGUESE("Português", "pt")
    }

    private lateinit var pipelineKey: String
    val message = mutableStateOf("")
    val language = mutableStateOf(defaultLanguage)
    val error = mutableStateOf<Exception?>(null)

    fun post() = async {
        try {
            error.value = null
            client.post(message = message.value, language = language.value.code)
            cancel()
        } catch (ex: Exception) {
            println("Failed to post: ${ex.message}")
            error.value = ex
        }
    }

    fun cancel() = appPipeline.trigger(AppPipeline.Event.DISMISS)

    fun connect() = appPipeline.addEventListener(::handle).apply {
        pipelineKey = this
    }

    fun disconnect() {
        if (::pipelineKey.isInitialized.not()) return
        appPipeline.removeEventListener(pipelineKey)
    }

    private fun handle(event: AppPipeline.Event) {
        if (event != AppPipeline.Event.POST) return
        post()
    }
}