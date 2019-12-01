package de.halfbit.co2monitor.web

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import de.halfbit.co2monitor.graphql.createGraphQL
import graphql.GraphQL
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

fun createGraphqlHandler(): HttpHandler =
    GraphqlHandler(
        createGraphQL(),
        Moshi.Builder().build()
    )

private class GraphqlHandler(
    private val graphql: GraphQL,
    moshi: Moshi
) : HttpHandler {

    private val requestAdapter = moshi.adapter(GraphqlRequest::class.java)
    private val responseAdapter = moshi.adapter(Map::class.java)

    override fun invoke(req: Request): Response {
        val graphqlRequest = checkNotNull(requestAdapter.fromJson(req.bodyString()))
        val result = graphql.execute(graphqlRequest.query).toSpecification()
        return Response(Status.OK).body(responseAdapter.toJson(result))
    }
}

@JsonClass(generateAdapter = true)
internal data class GraphqlRequest(val query: String)
