package de.halfbit.co2monitor.graphql.fetchers

import de.halfbit.co2monitor.Co2Database
import de.halfbit.co2monitor.domain.Measurement
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

internal class CurrentMeasurementFetcher(
    private val database: Co2Database
) : DataFetcher<Measurement> {

    override fun get(environment: DataFetchingEnvironment): Measurement? =
        database.transactionWithResult {
            database.measurementQueries
                .selectMostCurrent()
                .executeAsOneOrNull()
                ?.toDomainMeasurement()
        }
}

private fun de.halfbit.co2monitor.Measurement.toDomainMeasurement() =
    Measurement(id, time, temperature, co2)
