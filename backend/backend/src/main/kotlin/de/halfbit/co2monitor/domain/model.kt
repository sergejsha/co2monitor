package de.halfbit.co2monitor.domain

import java.time.Instant

data class Measurement(
    val id: Int,
    val time: Instant,
    val temperature: Int,
    val co2: Int
)
