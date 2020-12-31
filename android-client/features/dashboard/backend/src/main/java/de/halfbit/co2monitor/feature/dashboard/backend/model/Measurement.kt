package de.halfbit.co2monitor.feature.dashboard.backend.model

import kotlinx.datetime.Instant

data class Measurement(
    val time: Instant,
    val co2: Int,
    val temperature: Int
)
