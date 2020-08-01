package de.halfbit.co2monitor.web

import com.squareup.moshi.Moshi
import graphql.GraphQL
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.util.pipeline.PipelineContext

internal fun Route.graphql(
    path: String,
    graphql: GraphQL,
    moshi: Moshi = Moshi.Builder().build()
): Route {

    val requestAdapter = moshi.adapter(GraphqlRequest::class.java)
    val responseAdapter = moshi.adapter(Map::class.java)

    val graphQLRoute: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit = {
        val body = call.receiveText()
        val graphqlRequest = checkNotNull(requestAdapter.fromJson(body))
        val result = graphql.execute(graphqlRequest.query).toSpecification()
        val graphqlResponse = responseAdapter.toJson(result)
        call.respondText(graphqlResponse, ContentType.Application.Json)
    }

    return route(path) {
        post(graphQLRoute)
    }
}
