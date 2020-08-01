package de.halfbit.co2monitor.database.adapters

import com.squareup.sqldelight.ColumnAdapter
import java.time.Instant

class InstantAdapter : ColumnAdapter<Instant, Long> {
    override fun encode(value: Instant) = value.toEpochMilli()
    override fun decode(databaseValue: Long): Instant = Instant.ofEpochMilli(databaseValue)
}
