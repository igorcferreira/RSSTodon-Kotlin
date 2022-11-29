package dev.igorcferreira.rsstodon.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.core.text.util.LinkifyCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.MaterialColors
import dev.igorcferreira.rsstodon.android.db.domain.PersistedStatusRepository
import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.api.model.Configuration
import dev.igorcferreira.rsstodon.ui.views.events.AppPipeline
import dev.igorcferreira.rsstodon.ui.views.launcher.Splash
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URI

class MainActivity : ComponentActivity() {

    private lateinit var client: MastodonClient
    private lateinit var appPipeline: AppPipeline
    private lateinit var pipelineKey: String
    private var codeResponse: ((code: String) -> Unit)? = null
    private val isRefreshing = mutableStateOf(false)

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialise(this)

        setContent {
            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing.value,
                onRefresh = ::refresh
            )

            Box(modifier = Modifier.pullRefresh(pullRefreshState), contentAlignment = Alignment.TopCenter) {
                Splash(client = client, appPipeline = appPipeline)
                PullRefreshIndicator(refreshing = isRefreshing.value, state = pullRefreshState)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appPipeline.removeEventListener(pipelineKey)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.getQueryParameter("code")?.let { code ->
            codeResponse?.let { it(code) }
        }
        codeResponse = null
    }

    private fun initialise(context: Context) {
        client = MastodonClient(Configuration(
            instance = BuildConfig.INSTANCE,
            authentication = Configuration.Authentication(
                clientId = BuildConfig.CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                scope = BuildConfig.SCOPE,
                redirectScheme = BuildConfig.REDIRECT_SCHEME,
                tokenStorage = TokenStorage(context)
            )
        ), PersistedStatusRepository(context))
        appPipeline = AppPipeline(
            stringFormatter = { WebView(content = it) },
            urlLauncher = ::open,
            printLogs = BuildConfig.DEBUG
        )

        pipelineKey = appPipeline.addEventListener(::handle)

        onBackPressedDispatcher.addCallback(owner = this) {
            appPipeline.trigger(AppPipeline.Event.DISMISS)
        }
    }

    private fun refresh() = lifecycleScope.launch {
        isRefreshing.value = true
        appPipeline.trigger(AppPipeline.Event.REFRESH)
        delay(300L)
        isRefreshing.value = false
    }

    @Composable
    private fun WebView(content: String) {
        AndroidView({ context -> TextView(context).apply {
            text = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY)
            setTextColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnBackground))
            LinkifyCompat.addLinks(this, Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES)
        } })
    }

    private fun handle(event: AppPipeline.Event) {
        if (event != AppPipeline.Event.CLOSE) return
        finish()
    }

    private fun open(uri: URI, update: (code: String) -> Unit) {
        codeResponse = update
        val url = Uri.parse(uri.toString()) ?: return
        val launcher = CustomTabsIntent.Builder().apply {
            setInstantAppsEnabled(true)
            setShareState(CustomTabsIntent.SHARE_STATE_OFF)
        }.build()
        launcher.intent.putExtra(
            Intent.EXTRA_REFERRER,
            Uri.parse("android-app://$packageName")
        )
        launcher.launchUrl(this, url)
    }
}