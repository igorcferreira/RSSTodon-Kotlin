package dev.igorcferreira.rsstodon.android

import android.content.Context
import dev.igorcferreira.rsstodon.api.domain.ITokenStorage

data class TokenStorage(
    private val preferences: Preferences,
): ITokenStorage {

    constructor(
        context: Context
    ): this(Preferences(context))

    override var token: String?
        get() = preferences.token
        set(value) { preferences.token = value }
}