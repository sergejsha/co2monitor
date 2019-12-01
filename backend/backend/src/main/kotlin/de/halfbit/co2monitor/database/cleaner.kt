package de.halfbit.co2monitor.database

import de.halfbit.co2monitor.Arguments
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.concurrent.fixedRateTimer

fun createDatabaseCleaner(cleaner: Arguments.Database.Cleaner): Timer =
    fixedRateTimer(
        name = "Co2Monitor database cleaner",
        startAt = Date(Instant.now().plus(5, ChronoUnit.HOURS).toEpochMilli()),
        period = Duration.ofDays(cleaner.periodDays.toLong()).toMillis(),
        action = {
            transaction {
                val threshold = (Instant.now().minus(cleaner.retentionDays.toLong(), ChronoUnit.DAYS)).toEpochMilli()
                Measurements.deleteWhere { Measurements.time less threshold }
            }
        }
    )