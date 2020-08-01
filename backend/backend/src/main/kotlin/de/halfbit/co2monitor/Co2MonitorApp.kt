package de.halfbit.co2monitor

import de.halfbit.co2monitor.database.createDatabase
import de.halfbit.co2monitor.graphql.createGraphql
import de.halfbit.co2monitor.web.graphql
import io.ktor.application.Application
import io.ktor.routing.routing

private val database = createDatabase()
private val graphql = createGraphql(database)

fun Application.main() {
    routing {
        graphql("/graphql", graphql)
    }
}
