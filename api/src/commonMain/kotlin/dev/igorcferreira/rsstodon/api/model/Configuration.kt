package dev.igorcferreira.rsstodon.api.model

import dev.igorcferreira.rsstodon.api.domain.ITokenStorage
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class Configuration constructor(
    val instance: String,
    val authentication: Authentication? = null
) {
    @Serializable
    data class Authentication constructor(
        val clientId: String,
        val clientSecret: String,
        val scope: String,
        val redirectScheme: String,
        val tokenStorage: ITokenStorage
    )

    val authorizationUri: URI?
        get() = authentication?.let {
            URI("${instance}/oauth/authorize?force_login=true&response_type=code&client_id=${it.clientId}&redirect_uri=${it.redirectScheme}://oauth&scope=${it.scope.replace(" ", "%20")}")
        }

}
