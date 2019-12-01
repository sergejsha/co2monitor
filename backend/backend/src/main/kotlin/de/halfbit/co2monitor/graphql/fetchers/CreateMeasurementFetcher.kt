package de.halfbit.co2monitor.graphql.fetchers

import de.halfbit.co2monitor.database.Measurements
import de.halfbit.co2monitor.domain.Measurement
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

object CreateMeasurementFetcher : DataFetcher<Measurement> {

    override fun get(environment: DataFetchingEnvironment): Measurement {
        val measurement = environment.arguments["measurement"] as Map<*, *>
        val time = measurement["time"] as Instant
        val temperature = measurement["temperature"] as Int
        val co2 = measurement["co2"] as Int

        val result = transaction {
            Measurements.insert {
                it[Measurements.time] = time.toEpochMilli()
                it[Measurements.temperature] = temperature.toShort()
                it[Measurements.co2] = co2.toShort()
            }
        }

        return Measurement(
            id = result[Measurements.id],
            time = Instant.ofEpochMilli(result[Measurements.time]),
            temperature = result[Measurements.temperature].toInt(),
            co2 = result[Measurements.co2].toInt()
        )
    }
}