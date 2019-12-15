@file:JvmName("Main")

package de.halfbit.co2monitor

import de.halfbit.co2monitor.database.initializeDatabase
import de.halfbit.co2monitor.web.graphql
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    initialize {
        initializeDatabase(args.database)
        embeddedServer(Netty, port = args.server.port) {
            routing {
                graphql("/graphql", graphql, moshi)
            }
        }.run {
            start(wait = true)
        }
    }
}
