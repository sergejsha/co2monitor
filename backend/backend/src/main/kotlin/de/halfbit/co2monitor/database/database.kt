package de.halfbit.co2monitor.database

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import de.halfbit.co2monitor.Co2Database
import de.halfbit.co2monitor.Measurement
import de.halfbit.co2monitor.database.adapters.InstantAdapter
import java.io.File

fun createDatabase(): Co2Database {

    val databasePath = "data/co2monitor.db"
    File(databasePath).parentFile.mkdirs()

    return Co2Database(
        driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath").apply {
            Co2Database.Schema.create(this)
        },
        MeasurementAdapter = Measurement.Adapter(
            timeAdapter = InstantAdapter()
        )
    )
}
