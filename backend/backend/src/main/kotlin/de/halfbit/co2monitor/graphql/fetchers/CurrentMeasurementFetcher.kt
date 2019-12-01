package de.halfbit.co2monitor.graphql.fetchers

import de.halfbit.co2monitor.database.Measurements
import de.halfbit.co2monitor.domain.Measurement
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

internal object CurrentMeasurementFetcher : DataFetcher<Measurement> {

    override fun get(environment: DataFetchingEnvironment): Measurement? =
        transaction {
            Measurements
                .selectAll()
                .orderBy(Measurements.time to SortOrder.DESC)
                .limit(1, 0)
                .firstOrNull()
                ?.let {
                    Measurement(
                        id = it[Measurements.id],
                        time = Instant.ofEpochMilli(it[Measurements.time]),
                        temperature = it[Measurements.temperature].toInt(),
                        co2 = it[Measurements.co2].toInt()
                    )
                }
        }
}