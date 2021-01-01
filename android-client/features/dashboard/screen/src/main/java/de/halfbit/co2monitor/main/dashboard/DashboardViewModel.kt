package de.halfbit.co2monitor.main.dashboard

import de.halfbit.co2monitor.commons.StringRef
import de.halfbit.co2monitor.commons.stringRef
import de.halfbit.co2monitor.feature.dashboard.backend.RecentMeasurements
import de.halfbit.co2monitor.feature.dashboard.backend.RecentMeasurements.Data
import de.halfbit.co2monitor.feature.dashboard.backend.model.Measurement
import de.halfbit.co2monitor.main.mvi.RETAINED
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.scan
import magnet.Instance

interface DashboardViewModel {
    val state: Flow<State>
}

data class State(
    val loading: Boolean = false,
    val measurement: Measurement? = null,
    val error: StringRef? = null
)

@ExperimentalCoroutinesApi
@Instance(type = DashboardViewModel::class, limitedTo = RETAINED)
internal class DefaultDashboardViewModel(
    recentMeasurements: RecentMeasurements
) : DashboardViewModel {

    override val state: Flow<State> =
        channelFlow {
            while (true) {
                recentMeasurements.observe()
                    .scan(State()) { state, data ->
                        when (data) {
                            Data.Loading -> {
                                state.copy(loading = true)
                            }
                            is Data.Success -> {
                                state.copy(
                                    loading = false,
                                    measurement = data.measurement
                                )
                            }
                            is Data.Failure -> {
                                state.copy(
                                    loading = false,
                                    error = stringRef(R.string.error_generic)
                                )
                            }
                        }
                    }
            }
        }
}
