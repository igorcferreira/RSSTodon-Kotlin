package dev.igorcferreira.rsstodon.api

import dev.igorcferreira.rsstodon.api.domain.ITokenStorage
import dev.igorcferreira.rsstodon.api.domain.NetworkDataSource
import dev.igorcferreira.rsstodon.api.model.Configuration
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestMastodonClient {

    private val postResponse = "{\"id\":\"109360780985464227\",\"created_at\":\"2022-11-17T19:24:48.768Z\",\"in_reply_to_id\":null,\"in_reply_to_account_id\":null,\"sensitive\":false,\"spoiler_text\":\"\",\"visibility\":\"public\",\"language\":\"pt\",\"uri\":\"https://mastodon.social/users/igorcferreira/statuses/109360780985464227\",\"url\":\"https://mastodon.social/@igorcferreira/109360780985464227\",\"replies_count\":0,\"reblogs_count\":0,\"favourites_count\":0,\"edited_at\":null,\"favourited\":false,\"reblogged\":false,\"muted\":false,\"bookmarked\":false,\"pinned\":false,\"content\":\"\\u003cp\\u003eEu até que gostei da API do mastodon. Mas, não ver uma opção de Refresh Token é meio bizarro.\\u003c/p\\u003e\",\"filtered\":[],\"reblog\":null,\"application\":{\"name\":\"Multipeer Post\",\"website\":\"https://igorcferreira.dev\"},\"account\":{\"id\":\"414596\",\"username\":\"igorcferreira\",\"acct\":\"igorcferreira\",\"display_name\":\"Igor Ferreira\",\"locked\":false,\"bot\":false,\"discoverable\":true,\"group\":false,\"created_at\":\"2018-08-11T00:00:00.000Z\",\"note\":\"\\u003cp\\u003eEle/Dele He/Him - INFP-T. Whovian who loves to walk, beer, music, comics and tech. Opinions expressed here are my own, unless it is about how good coffee is. Coffee being good is a fact.\\u003c/p\\u003e\",\"url\":\"https://mastodon.social/@igorcferreira\",\"avatar\":\"https://files.mastodon.social/accounts/avatars/000/414/596/original/09124b83e99fbd08.jpeg\",\"avatar_static\":\"https://files.mastodon.social/accounts/avatars/000/414/596/original/09124b83e99fbd08.jpeg\",\"header\":\"https://files.mastodon.social/accounts/headers/000/414/596/original/1d0c60e1f754ade77a61c7df70f6571f.jpeg\",\"header_static\":\"https://files.mastodon.social/accounts/headers/000/414/596/original/1d0c60e1f754ade77a61c7df70f6571f.jpeg\",\"followers_count\":21,\"following_count\":39,\"statuses_count\":123,\"last_status_at\":\"2022-11-24\",\"noindex\":true,\"emojis\":[],\"fields\":[{\"name\":\"Blog pessoal\",\"value\":\"\\u003ca href=\\\"https://igorcferreira.com\\\" target=\\\"_blank\\\" rel=\\\"nofollow noopener noreferrer me\\\"\\u003e\\u003cspan class=\\\"invisible\\\"\\u003ehttps://\\u003c/span\\u003e\\u003cspan class=\\\"\\\"\\u003eigorcferreira.com\\u003c/span\\u003e\\u003cspan class=\\\"invisible\\\"\\u003e\\u003c/span\\u003e\\u003c/a\\u003e\",\"verified_at\":\"2022-10-30T03:02:52.504+00:00\"},{\"name\":\"Blog development\",\"value\":\"\\u003ca href=\\\"https://igorcferreira.dev\\\" target=\\\"_blank\\\" rel=\\\"nofollow noopener noreferrer me\\\"\\u003e\\u003cspan class=\\\"invisible\\\"\\u003ehttps://\\u003c/span\\u003e\\u003cspan class=\\\"\\\"\\u003eigorcferreira.dev\\u003c/span\\u003e\\u003cspan class=\\\"invisible\\\"\\u003e\\u003c/span\\u003e\\u003c/a\\u003e\",\"verified_at\":\"2022-10-30T03:02:52.934+00:00\"}]},\"media_attachments\":[],\"mentions\":[],\"tags\":[],\"emojis\":[],\"card\":null,\"poll\":null}"

    @Test
    fun `when non-authenticated, post should fail`() {
        val authentication = mockk<Configuration.Authentication>()
        val storage = mockk<ITokenStorage>()
        val configuration = mockk<Configuration>()
        val dataSource = NetworkDataSource(
            configuration = configuration,
            httpClient = HttpClient(MockEngine { respond(content = postResponse) })
        )

        every { configuration.authentication } returns authentication
        every { authentication.tokenStorage } returns storage
        every { storage.token } returns null

        val client = MastodonClient(configuration = configuration, dataSource = dataSource)
        assertThrows(MissingTokenException::class.java) {
            runBlocking { client.post(message = "") }
        }
    }

    @Test
    fun `when non-authenticated, client should make upper layers aware of it`() {
        val configuration = mockk<Configuration>()

        every { configuration.authentication } returns null

        val client = MastodonClient(configuration = configuration)

        assertFalse(client.isAuthenticated)
    }

    @Test
    fun `when authenticated with an empty string, client should inform upper layers that the code is unauthorized`() {
        val authentication = mockk<Configuration.Authentication>()
        val storage = mockk<ITokenStorage>()
        val configuration = mockk<Configuration>()
        val dataSource = NetworkDataSource(
            configuration = configuration,
            httpClient = HttpClient(MockEngine { respond(content = postResponse) })
        )

        val client = MastodonClient(configuration = configuration, dataSource = dataSource)

        every { configuration.authentication } returns authentication
        every { authentication.tokenStorage } returns storage
        every { storage.token } returns ""

        assertFalse(client.isAuthenticated)
    }

    @Test
    fun `when token not available, client should inform upper layers that the code is unauthorized`() {
        val authentication = mockk<Configuration.Authentication>()
        val storage = mockk<ITokenStorage>()
        val configuration = mockk<Configuration>()
        val dataSource = NetworkDataSource(
            configuration = configuration,
            httpClient = HttpClient(MockEngine { respond(content = postResponse) })
        )

        val client = MastodonClient(configuration = configuration, dataSource = dataSource)

        every { configuration.authentication } returns authentication
        every { authentication.tokenStorage } returns storage
        every { storage.token } returns null

        assertFalse(client.isAuthenticated)
    }

    @Test
    fun `when authenticated, client should upper layers aware of it`() {
        val authentication = mockk<Configuration.Authentication>()
        val storage = mockk<ITokenStorage>()
        val configuration = mockk<Configuration>()
        val dataSource = NetworkDataSource(
            configuration = configuration,
            httpClient = HttpClient(MockEngine { respond(content = postResponse) })
        )

        val client = MastodonClient(configuration = configuration, dataSource = dataSource)

        every { configuration.authentication } returns authentication
        every { authentication.tokenStorage } returns storage
        every { storage.token } returns "TOKEN"

        assertTrue(client.isAuthenticated)
    }

    @Test
    fun `when authenticated, post should return formatted post`() {
        val authentication = mockk<Configuration.Authentication>()
        val storage = mockk<ITokenStorage>()
        val configuration = mockk<Configuration>()
        val dataSource = NetworkDataSource(
            configuration = configuration,
            httpClient = HttpClient(MockEngine { respond(content = postResponse) })
        )

        every { configuration.authentication } returns authentication
        every { configuration.instance } returns "https://example.com"
        every { authentication.tokenStorage } returns storage
        every { storage.token } returns "TOKEN"

        val client = MastodonClient(configuration = configuration, dataSource = dataSource)
        val response = runBlocking { client.post(message = "") }

        assertEquals("109360780985464227", response.id.value)
    }

    @Test
    fun `client should delegate authentication to data source`() {
        val configuration = mockk<Configuration>()
        val dataSource = mockk<NetworkDataSource>()

        coEvery { dataSource.authenticate(any()) } returns Unit

        val client = MastodonClient(configuration = configuration, dataSource = dataSource)

        runBlocking { client.authenticate("CODE") }

        coVerify(exactly = 1) { dataSource.authenticate(eq("CODE")) }
    }

    @Test
    fun `even non-authorized status should return`() {
        val configuration = mockk<Configuration>()
        val dataSource = NetworkDataSource(
            configuration = configuration,
            httpClient = HttpClient(MockEngine { respond(content = postResponse) })
        )

        every { configuration.authentication } returns null
        every { configuration.instance } returns "https://example.com"

        val client = MastodonClient(configuration = configuration, dataSource = dataSource)
        val response = runBlocking { client.status(id = "").first() }

        assertEquals("109360780985464227", response.id)
    }

    @Test
    fun `client should return a computed URI from authentication`() {
        val authentication = Configuration.Authentication(
            clientId = "CLIENT_ID",
            clientSecret = "CLIENT_SECRET",
            scope = "SCOPE",
            redirectScheme = "SCHEME",
            tokenStorage = object : ITokenStorage {
                override var token: String?
                    get() = null
                    set(_) = Unit
            }
        )
        val client = MastodonClient(Configuration("https://www.example.com", authentication))

        assertEquals("https://www.example.com/oauth/authorize?force_login=true&response_type=code&client_id=CLIENT_ID&redirect_uri=SCHEME://oauth&scope=SCOPE", client.authorizationURI?.toString())
    }

}