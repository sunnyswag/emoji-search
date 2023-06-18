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
    @SerializedName("embedding") val embedding: List<Double>,
    @SerializedName("index") val index: Int,
)

data class Usage(

    /** Count of prompts tokens. */
    @SerializedName("prompt_tokens") val promptTokens: Int? = null,

    /** Count of completion tokens. */
    @SerializedName("completion_tokens") val completionTokens: Int? = null,

    /** Count of total tokens. */
    @SerializedName("total_tokens") val totalTokens: Int? = null,
)