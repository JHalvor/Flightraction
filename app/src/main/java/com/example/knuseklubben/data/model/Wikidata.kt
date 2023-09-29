package com.example.knuseklubben.data.model

import kotlinx.serialization.Serializable

/**
 * This data class is for the image we get from API-call.
 */
@Serializable
data class WikidataImages(
    val query: Query?
)

@Serializable
data class Query(
    val pages: Map<String, Page>?
)

@Serializable
data class Page(
    val title: String?,
    val images: List<Image>?
)

@Serializable
data class Image(
    val title: String?
)

/**
 * This data class is for the description we get from API-call.
 */
@Serializable
data class WikidataDescriptions(
    val entities: Map<String, WikidataEntity>?
)

@Serializable
data class WikidataEntity(
    val labels: Map<String, Label>?,
    val descriptions: Map<String, Description>?
)

@Serializable
data class Label(
    val language: String?,
    val value: String?
)

@Serializable
data class Description(
    val language: String?,
    val value: String?
)
