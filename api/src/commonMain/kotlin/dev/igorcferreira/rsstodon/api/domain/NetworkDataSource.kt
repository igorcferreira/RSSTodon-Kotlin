package dev.igorcferreira.rsstodon.api.domain

import dev.igorcferreira.rsstodon.api.model.Configuration
import dev.igorcferreira.rsstodon.api.model.request.Authentication
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URL

@OptIn(ExperimentalSerializationApi::class)
internal class NetworkDataSource(
    private val configuration: Configuration,
    private val json: Json = defaultJsonConfiguration,
    private val httpClient: HttpClient = buildHttpClient()
) {

    constructor(
        configuration: Configuration
    ): this(configuration, defaultJsonConfiguration)

    internal suspend fun authenticate(code: String) {
        val authentication = configuration.authentication
            ?: throw Exception("Missing authentication configuration")
        configuration.authentication.tokenStorage.token = post(
            Authentication(code, authentication),
            Endpoint.GetAuthenticationToken
        ).token
    }

    internal suspend inline fun <reified Response> get(
        endpoint: Endpoint<Response>
    ): Response = perform<Unit, Response>(
        configuration.instance,
        HttpMethod.Get,
        null,
        endpoint
    )

    internal suspend inline fun <reified Body, reified Response> post(
        body: Body,
        endpoint: Endpoint<Response>
    ): Response = perform(
        configuration.instance,
        HttpMethod.Post,
        body,
        endpoint
    )

    private suspend inline fun <reified Body, reified Response> perform(
        baseUrl: String,
        method: HttpMethod,
        body: Body?,
        endpoint: Endpoint<Response>
    ): Response = httpClient.prepareRequest {
        this.method = method
        url(baseUrl.compose(endpoint.url))
        endpoint.headers.forEach { headers.append(it.key, it.value) }

        configuration.authentication?.tokenStorage?.token?.let {
            headers.append("Authorization", "Bearer $it")
        }

        body?.let {
            setBody(json.encodeToString(body))
            contentType(ContentType.Application.Json)
        }

        log()
    }.execute { response ->
        response.log()
        if (!response.status.isSuccess()) {
            throw Exception("Exception: ${response.status}")
        }
        json.decodeFromString(response.bodyAsText())
    }

    private fun String.compose(path: String): URL = URL(URL(this), path)

    private companion object {
        val defaultJsonConfiguration = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        fun buildHttpClient(): HttpClient = HttpClient()
    }
}

fun HttpRequestBuilder.log() {
    val components = mutableListOf("curl -i -X ${method.value} '${url.buildString()}'")
    headers.entries().forEach { entry ->
        components.add("-H '${entry.key}: ${entry.value.joinToString(",")}'")
    }
    if (body !is NullBody && body !is EmptyContent) {
        components.add("-d '$body'")
    }

    println(components.joinToString(" \\\n"))
}

fun HttpResponse.log() = println("<-- ${status.value} ${request.url}")