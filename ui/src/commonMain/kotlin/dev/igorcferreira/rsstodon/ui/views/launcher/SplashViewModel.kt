package dev.igorcferreira.rsstodon.ui.views.launcher

import androidx.compose.runtime.mutableStateOf
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import dev.igorcferreira.rsstodon.ui.views.support.async

class SplashViewModel(
    val client: MastodonClient,
    val appPipeline: AppPipeline
) {
    sealed class ViewState {
        object LoggedOut: ViewState()
        object LoggedIn: ViewState()
        class Loading(val message: String): ViewState()
        class Error(val message: String): ViewState()
    }

    val logged = mutableStateOf(if (client.isAuthenticated) {
        ViewState.LoggedIn
    } else {
        ViewState.LoggedOut
    })

    private fun update(code: String) = async {
        if (code.isEmpty()) {
            logged.value = ViewState.LoggedOut
            return@async
        }

        logged.value = try {
            client.authenticate(code)
            ViewState.LoggedIn
        } catch (ex: Exception) {
            ViewState.Error(ex.localizedMessage ?: "")
        }
    }

    fun login() = async {
        val uri = client.authorizationURI ?: return@async
        logged.value = try {
            appPipeline.launch(uri, ::update)
            ViewState.Loading("Authenticating")
        } catch (ex: Exception) {
            ViewState.Error(ex.localizedMessage ?: "")
        }
    }

    fun retry () {
        logged.value = ViewState.LoggedOut
    }
}