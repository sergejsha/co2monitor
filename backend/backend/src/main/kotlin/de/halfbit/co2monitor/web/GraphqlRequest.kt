package de.halfbit.co2monitor.web

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GraphqlRequest(val query: String)
