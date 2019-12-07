package de.halfbit.co2monitor.main.dashboard

import de.halfbit.co2monitor.commons.Option
import de.halfbit.co2monitor.commons.StringRef
import de.halfbit.co2monitor.commons.network.NetworkObserver
import de.halfbit.co2monitor.commons.stringRef
import de.halfbit.co2monitor.main.mvi.RETAINED
import de.halfbit.co2monitor.repo.measurement.Measurement
import de.halfbit.co2monitor.repo.measurement.MeasurementRepository
import de.halfbit.knot.Knot
import de.halfbit.knot.knot
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import magnet.Instance
import org.threeten.bp.Instant
import java.io.IOException
import java.util.concurrent.TimeUnit

interface DashboardViewModel {
    val state: Observable<State>
    val changes: Consumer<Change>
}

data class State(
    val loading: Loading = Loading.None,
    val measurement: Measurement? = null,
    val failure: Failure? = null
)

enum class Loading {
    None, Visible, Invisible
}

data class Failure(
    val error: StringRef,
    val failingSince: Instant
)

sealed class Change {
    // public changes go into here
}

@Instance(type = DashboardViewModel::class, limitedTo = RETAINED)
internal class DefaultDashboardViewModel @JvmOverloads constructor(
    repository: MeasurementRepository,
    networkObserver: NetworkObserver,
    timerScheduler: Scheduler = Schedulers.computation()
) : DashboardViewModel {

    private val knot: Knot<State, Change> = createKnot(
        repository,
        networkObserver,
        timerScheduler
    )

    override val state: Observable<State> = knot.state
    override val changes: Consumer<Change> = knot.change
}

private fun createKnot(
    repository: MeasurementRepository,
    networkObserver: NetworkObserver,
    timerScheduler: Scheduler
) = knot<State, Change, Action> {
    state { initial = State() }

    changes {
        reduceOn = Schedulers.computation()

        reduce { change ->
            when (change) {

                OnTimeToUpdateMeasurement -> {
                    val state =
                        if (isMeasurementInvalid) {
                            copy(measurement = null, loading = loading.makeVisible())
                        } else this

                    if (shouldUpdateCurrentMeasurement) {
                        val loading =
                            if (state.measurement == null) Loading.Visible
                            else Loading.Invisible

                        state.copy(loading = loading) + Action.LoadMeasurement
                    } else state.only
                }

                is OnLoadingSuccess ->
                    copy(
                        loading = Loading.None,
                        measurement = change.measurement.optional,
                        failure = null
                    ).only

                is OnLoadingFailure -> {
                    val failingSince = failure?.failingSince ?: Instant.now()
                    val measurement = if (isMeasurementInvalid) null else measurement
                    copy(
                        loading = Loading.None,
                        measurement = measurement,
                        failure = Failure(
                            error = humanize(change.error),
                            failingSince = failingSince
                        )
                    ).only
                }
            }
        }
    }

    actions {
        perform<Action.LoadMeasurement> {
            switchMapSingle {
                repository
                    .getCurrentMeasurement()
                    .map<Change> { OnLoadingSuccess(it) }
                    .onErrorReturn { OnLoadingFailure(it) }
            }
        }
    }

    events {
        coldSource {
            Observable
                .interval(0L, MEASUREMENT_REFRESH_SECS, TimeUnit.SECONDS, timerScheduler)
                .map<Change> { OnTimeToUpdateMeasurement }
        }
        coldSource {
            networkObserver.networkAvailable
                .map { OnTimeToUpdateMeasurement }
        }
    }
}

private fun Loading.makeVisible(): Loading =
    if (this == Loading.Invisible) Loading.Visible else this

private val State.isMeasurementInvalid: Boolean
    get() = when (failure) {
        null -> false
        else -> failure.failingSince
            .plusSeconds(MEASUREMENT_MAX_VALIDITY_SECS)
            .isBefore(Instant.now())
    }

private val State.shouldUpdateCurrentMeasurement: Boolean
    get() = when {
        loading != Loading.None -> false
        measurement == null -> true
        else -> measurement.time
            .plusSeconds(MEASUREMENT_MIN_VALIDITY_SECS)
            .isBefore(Instant.now())
    }

private const val MEASUREMENT_MIN_VALIDITY_SECS = 5L
private const val MEASUREMENT_MAX_VALIDITY_SECS = 15L
private const val MEASUREMENT_REFRESH_SECS = 10L

private sealed class Action {
    object LoadMeasurement : Action()
}

private object OnTimeToUpdateMeasurement : Change()
private data class OnLoadingSuccess(val measurement: Option<Measurement>) : Change()
private data class OnLoadingFailure(val error: Throwable) : Change()

private fun humanize(error: Throwable): StringRef =
    when {
        error.isNetworkError() -> stringRef(R.string.error_no_network)
        else -> stringRef(R.string.error_generic)
    }

private fun Throwable.isNetworkError(): Boolean =
    this is IOException || cause is IOException
