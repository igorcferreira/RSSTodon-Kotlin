package dev.igorcferreira.rsstodon.app.domain

import dev.igorcferreira.rsstodon.api.domain.ITokenStorage
import pt.davidafsilva.apple.OSXKeychain
import kotlin.jvm.optionals.getOrNull

sealed class TokenStorage: ITokenStorage {

    private class InMemoryTokenStorage(
        override var token: String? = null
    ): TokenStorage()

    private class KeychainTokenStorage(
        private val keychain: OSXKeychain
    ): TokenStorage() {
        override var token: String?
            get() = keychain.findGenericPassword(service, account).getOrNull()
            set(value) = keychain.addGenericPassword(service, account, value)

        companion object {
            const val service = "dev.igorcferreira.rsstodon.app"
            const val account = "token"
        }
    }

    companion object {
        fun getInstance(): TokenStorage = try {
            KeychainTokenStorage(OSXKeychain.getInstance())
        } catch (ex: Exception) {
            InMemoryTokenStorage()
        }
    }
}