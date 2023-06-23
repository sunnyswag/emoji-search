package com.example.emojisemanticsearch.entity

import com.google.gson.annotations.SerializedName

/**
 * Create embeddings response.
 */
data class EmbeddingResponse(

    /** An embedding results. */
    @SerializedName("data") val embeddings: List<Embedding>,

    /** Embedding usage data. */
    @SerializedName("usage") val usage: Usage,
)

/**
 * An embedding result.
 * [documentation](https://beta.openai.com/docs/api-reference/embeddings)
 */
data class Embedding(
    @SerializedName("embedding") val embedding: FloatArray,
    @SerializedName("index") val index: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Embedding

        if (!embedding.contentEquals(other.embedding)) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = embedding.contentHashCode()
        result = 31 * result + index
        return result
    }
}

data class Usage(

    /** Count of prompts tokens. */
    @SerializedName("prompt_tokens") val promptTokens: Int? = null,

    /** Count of completion tokens. */
    @SerializedName("completion_tokens") val completionTokens: Int? = null,

    /** Count of total tokens. */
    @SerializedName("total_tokens") val totalTokens: Int? = null,
)