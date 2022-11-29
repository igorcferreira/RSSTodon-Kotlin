package dev.igorcferreira.rsstodon.api.model.response

import dev.igorcferreira.rsstodon.api.model.serializer.IdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = IdSerializer::class)
data class Id(
    val value: String
) {
    override fun equals(other: Any?): Boolean = (other as? String)?.equals(value)
        ?: (other as? Id)?.value?.equals(value)
        ?: false

    override fun toString(): String = value
    override fun hashCode(): Int {
        return value.hashCode()
    }
}
