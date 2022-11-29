package dev.igorcferreira.rsstodon.api.model.request

import dev.igorcferreira.rsstodon.api.model.Configuration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Authentication(
    @SerialName("grant_type") val grantType: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String,
    @SerialName("scope") val scope: String,
    @SerialName("code") val code: String,
    @SerialName("redirect_uri") val redirectUri: String
) {
    constructor(
        code: String,
        authentication: Configuration.Authentication
    ): this(
        grantType = "authorization_code",
        clientId = authentication.clientId,
        clientSecret = authentication.clientSecret,
        scope = authentication.scope,
        code = code,
        redirectUri = "${authentication.redirectScheme}://oauth"
    )
}