package de.halfbit.co2monitor.graphql.fetchers

import de.halfbit.co2monitor.Co2Database
import de.halfbit.co2monitor.domain.Measurement
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import java.time.Instant

internal class CreateMeasurementFetcher(
    private val database: Co2Database
) : DataFetcher<Measurement> {

    override fun get(environment: DataFetchingEnvironment): Measurement {

        val measurement = environment.arguments["measurement"] as Map<*, *>
        val time = measurement["time"] as Instant
        val temperature = measurement["temperature"] as Int
        val co2 = measurement["co2"] as Int

        val id: Int = database.transactionWithResult {
            database.measurementQueries.insert(time, temperature, co2)
            database.measurementQueries.selectIdByTime(time).executeAsOne()
        }

        return Measurement(id, time, temperature, co2)
    }
}