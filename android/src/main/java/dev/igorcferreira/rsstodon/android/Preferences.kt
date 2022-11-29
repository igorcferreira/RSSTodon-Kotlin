package dev.igorcferreira.rsstodon.android

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class Preferences(
    context: Context
) {
    private val container = EncryptedSharedPreferences.create(
        context,
        FILENAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    var token: String?
        get() = container.getString(TOKEN_KEY, null)
        set(value) { container.edit { putString(TOKEN_KEY, value) }}

    companion object {
        const val FILENAME = "shared_prefs"
        const val TOKEN_KEY = "dev.igorcferreira.rsstodon.android.TOKEN_KEY"
    }
}