@file:JvmName("Main")

package de.halfbit.co2monitor

import de.halfbit.co2monitor.database.initializeDatabase
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Netty
import org.http4k.server.asServer

fun main() {
    initialize {
        initializeDatabase(args.database)
        routes(
            "/graphql" bind POST to graphqlHandler
        ).run {
            asServer(
                Netty(port = args.server.port)
            ).start()
        }
    }
}
