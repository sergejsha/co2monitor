package de.halfbit.co2monitor.repo.measurement

import de.halfbit.co2monitor.commons.Option
import io.reactivex.Single
import org.threeten.bp.Instant

interface MeasurementRepository {
    fun getCurrentMeasurement(): Single<Option<Measurement>>
}

data class Measurement(
    val time: Instant,
    val co2: Int,
    val temperature: Int
)
