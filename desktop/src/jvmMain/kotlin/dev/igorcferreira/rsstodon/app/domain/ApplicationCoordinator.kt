package dev.igorcferreira.rsstodon.app.domain

import androidx.compose.runtime.mutableStateListOf
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.api.model.Configuration
import dev.igorcferreira.rsstodon.api.repository.InMemoryStatusRepository
import dev.igorcferreira.rsstodon.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.awt.desktop.OpenURIEvent
import java.awt.desktop.OpenURIHandler
import java.net.URI

class ApplicationCoordinator(
    private val exitStrategy: () -> Unit
) {
    enum class WindowType {
        SPLASH,
        POST
    }

    class OAuthCodeHandler(
        private val onAuthorized: (code: String) -> Unit
    ): OpenURIHandler {
        override fun openURI(event: OpenURIEvent?) {
            val query = event?.uri?.query ?: return
            val elements = query.split("=")
            val code = elements.lastOrNull() ?: return
            onAuthorized(code)
        }
    }

    val windowState = mutableStateListOf<WindowType>().apply {
        add(WindowType.SPLASH)
    }
    val client = MastodonClient(configuration = Configuration(
        instance = BuildConfig.INSTANCE,
        authentication = Configuration.Authentication(
            clientId = BuildConfig.CLIENT_ID,
            clientSecret = BuildConfig.CLIENT_SECRET,
            scope = BuildConfig.SCOPE,
            redirectScheme = BuildConfig.REDIRECT_SCHEME,
            tokenStorage = TokenStorage.getInstance()
        )
    ), InMemoryStatusRepository())

    suspend fun open(uri: URI, update: (code: String) -> Unit) {
        if (!Desktop.isDesktopSupported()) return
        val desktop = Desktop.getDesktop() ?: return
        desktop.setOpenURIHandler(OAuthCodeHandler(update))
        withContext(Dispatchers.IO) { desktop.browse(uri) }
    }

    fun present(type: WindowType) = windowState.add(type)
    fun dismiss(type: WindowType) {
        windowState.remove(type)
        if (windowState.isEmpty()) { exitStrategy() }
    }
}