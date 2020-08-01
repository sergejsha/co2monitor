package de.halfbit.co2monitor.database

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import de.halfbit.co2monitor.Co2Database
import de.halfbit.co2monitor.Measurement
import de.halfbit.co2monitor.database.adapters.InstantAdapter

fun createDatabase() =
    Co2Database(
        driver = JdbcSqliteDriver("jdbc:sqlite:data/co2monitor.db").apply {
            Co2Database.Schema.create(this)
        },
        MeasurementAdapter = Measurement.Adapter(
            timeAdapter = InstantAdapter()
        )
    )
