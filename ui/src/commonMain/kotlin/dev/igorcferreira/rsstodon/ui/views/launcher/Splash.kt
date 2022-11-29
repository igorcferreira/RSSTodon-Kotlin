package dev.igorcferreira.rsstodon.ui.views.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.ui.views.app.App
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import dev.igorcferreira.rsstodon.ui.views.login.Login
import dev.igorcferreira.rsstodon.ui.views.support.Error
import dev.igorcferreira.rsstodon.ui.views.support.Loading
import dev.igorcferreira.rsstodon.ui.views.theme.AppTheme

@Composable
fun Splash(
    client: MastodonClient,
    appPipeline: AppPipeline
) = Splash(SplashViewModel(client, appPipeline))

@Composable
fun Splash(
    viewModel: SplashViewModel
) = AppTheme {
    val logged = remember { viewModel.logged }

    when (val currentState = logged.value) {
        is SplashViewModel.ViewState.LoggedOut -> Login(viewModel::login)
        is SplashViewModel.ViewState.LoggedIn -> App(viewModel.client, viewModel.appPipeline)
        is SplashViewModel.ViewState.Error -> Error(message = currentState.message, viewModel::retry)
        is SplashViewModel.ViewState.Loading -> Loading(currentState.message)
    }
}