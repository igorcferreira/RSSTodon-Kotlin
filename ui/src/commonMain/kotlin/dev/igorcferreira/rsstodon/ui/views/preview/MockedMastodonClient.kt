package dev.igorcferreira.rsstodon.ui.views.preview

import dev.igorcferreira.rsstodon.api.MastodonClient
import dev.igorcferreira.rsstodon.api.model.Configuration
import dev.igorcferreira.rsstodon.api.model.presentation.StatusContent
import dev.igorcferreira.rsstodon.api.model.response.Id
import dev.igorcferreira.rsstodon.api.model.response.ScheduledStatus
import dev.igorcferreira.rsstodon.api.model.response.Status
import dev.igorcferreira.rsstodon.api.model.response.Visibility
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URI
import java.util.*

internal class MockedMastodonClient(
    override val isAuthenticated: Boolean = true
): MastodonClient(Configuration("")) {

    override val authorizationURI: URI
        get() = URI("https://www.example.com")

    override suspend fun authenticate(code: String) = Unit
    override suspend fun refreshTimeline(public: Boolean) = Unit
    override fun getTimelineFlow(): Flow<List<StatusContent>> {
        println("Requested timeline")
        return flowOf(listOf(MOCKED_STATUS_CONTENT))
    }
    override suspend fun status(id: String): Flow<StatusContent> = flowOf(MOCKED_STATUS_CONTENT)
    override suspend fun post(
        message: String,
        key: String,
        language: String?,
        visibility: Visibility?
    ): Status = MOCKED_STATUS

    override suspend fun schedule(
        message: String,
        date: Date,
        key: String,
        language: String?,
        visibility: Visibility?
    ): ScheduledStatus = ScheduledStatus(id = Id(""), Date(), MOCKED_PARAMS, null)

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
        val MOCKED_PARAMS = ScheduledStatus.Params(
            text = "",
            visibility = null,
            applicationId = null,
            replyTo = null,
            sensitive = null,
            spoilerText = null,
            medias = null
        )
        val MOCKED_STATUS: Status = json.decodeFromString("{\"id\":\"109360780985464227\",\"created_at\":\"2022-11-17T19:24:48.768Z\",\"in_reply_to_id\":null,\"in_reply_to_account_id\":null,\"sensitive\":false,\"spoiler_text\":\"\",\"visibility\":\"public\",\"language\":\"pt\",\"uri\":\"https://mastodon.social/users/igorcferreira/statuses/109360780985464227\",\"url\":\"https://mastodon.social/@igorcferreira/109360780985464227\",\"replies_count\":0,\"reblogs_count\":0,\"favourites_count\":0,\"edited_at\":null,\"favourited\":false,\"reblogged\":false,\"muted\":false,\"bookmarked\":false,\"pinned\":false,\"content\":\"\\u003cp\\u003eEu até que gostei da API do mastodon. Mas, não ver uma opção de Refresh Token é meio bizarro.\\u003c/p\\u003e\",\"filtered\":[],\"reblog\":null,\"application\":{\"name\":\"Multipeer Post\",\"website\":\"https://igorcferreira.dev\"},\"account\":{\"id\":\"414596\",\"username\":\"igorcferreira\",\"acct\":\"igorcferreira\",\"display_name\":\"Igor Ferreira\",\"locked\":false,\"bot\":false,\"discoverable\":true,\"group\":false,\"created_at\":\"2018-08-11T00:00:00.000Z\",\"note\":\"\\u003cp\\u003eEle/Dele He/Him - INFP-T. Whovian who loves to walk, beer, music, comics and tech. Opinions expressed here are my own, unless it is about how good coffee is. Coffee being good is a fact.\\u003c/p\\u003e\",\"url\":\"https://mastodon.social/@igorcferreira\",\"avatar\":\"https://files.mastodon.social/accounts/avatars/000/414/596/original/09124b83e99fbd08.jpeg\",\"avatar_static\":\"https://files.mastodon.social/accounts/avatars/000/414/596/original/09124b83e99fbd08.jpeg\",\"header\":\"https://files.mastodon.social/accounts/headers/000/414/596/original/1d0c60e1f754ade77a61c7df70f6571f.jpeg\",\"header_static\":\"https://files.mastodon.social/accounts/headers/000/414/596/original/1d0c60e1f754ade77a61c7df70f6571f.jpeg\",\"followers_count\":21,\"following_count\":39,\"statuses_count\":123,\"last_status_at\":\"2022-11-24\",\"noindex\":true,\"emojis\":[],\"fields\":[{\"name\":\"Blog pessoal\",\"value\":\"\\u003ca href=\\\"https://igorcferreira.com\\\" target=\\\"_blank\\\" rel=\\\"nofollow noopener noreferrer me\\\"\\u003e\\u003cspan class=\\\"invisible\\\"\\u003ehttps://\\u003c/span\\u003e\\u003cspan class=\\\"\\\"\\u003eigorcferreira.com\\u003c/span\\u003e\\u003cspan class=\\\"invisible\\\"\\u003e\\u003c/span\\u003e\\u003c/a\\u003e\",\"verified_at\":\"2022-10-30T03:02:52.504+00:00\"},{\"name\":\"Blog development\",\"value\":\"\\u003ca href=\\\"https://igorcferreira.dev\\\" target=\\\"_blank\\\" rel=\\\"nofollow noopener noreferrer me\\\"\\u003e\\u003cspan class=\\\"invisible\\\"\\u003ehttps://\\u003c/span\\u003e\\u003cspan class=\\\"\\\"\\u003eigorcferreira.dev\\u003c/span\\u003e\\u003cspan class=\\\"invisible\\\"\\u003e\\u003c/span\\u003e\\u003c/a\\u003e\",\"verified_at\":\"2022-10-30T03:02:52.934+00:00\"}]},\"media_attachments\":[],\"mentions\":[],\"tags\":[],\"emojis\":[],\"card\":null,\"poll\":null}")
        val MOCKED_STATUS_CONTENT = MOCKED_STATUS.let { StatusContent(it.id.value, "account", "uri", it.content) }
    }
}