package de.halfbit.co2monitor.feature.dashboard.backend

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main() = runBlocking {

    val recentMeasurements = DefaultRecentMeasurements(
        graphqlClient = provideApolloClient(
            httpClient = provideOkHttpClient()
        )
    )

    println("started!")
    recentMeasurements.observe().collect {
        println("collected: $it")
    }
    println("done!")
}