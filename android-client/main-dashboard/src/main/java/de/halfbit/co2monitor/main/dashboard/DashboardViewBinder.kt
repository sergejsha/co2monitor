package de.halfbit.co2monitor.main.dashboard

import de.halfbit.co2monitor.commons.None
import de.halfbit.co2monitor.commons.toOption
import de.halfbit.co2monitor.main.mvi.LifecycleViewBinder
import de.halfbit.co2monitor.main.mvi.ViewBinder
import io.reactivex.rxjava3.disposables.CompositeDisposable
import magnet.Instance

interface DashboardViewBinder : ViewBinder

@Instance(type = DashboardViewBinder::class)
internal class DefaultDashboardViewBinder(
    private val view: DashboardView,
    private val viewModel: DashboardViewModel
) : LifecycleViewBinder(), DashboardViewBinder {

    override fun bind(disposables: CompositeDisposable) {

        viewModel.state
            .distinctUntilChanged { t1, t2 -> t1.measurement?.co2 == t2.measurement?.co2 }
            .map {
                if (it.measurement?.co2 == null) "---"
                else it.measurement.co2.toString()
            }
            .into(view.co2Text, disposables)

        viewModel.state
            .distinctUntilChanged { t1, t2 -> t1.measurement?.co2 == t2.measurement?.co2 }
            .map {
                if (it.measurement?.co2 == null) Co2Level.Unknown
                else when {
                    it.measurement.co2 < 800 -> Co2Level.Good
                    it.measurement.co2 >= 1200 -> Co2Level.Bad
                    else -> Co2Level.Acceptable
                }
            }
            .into(view.co2Level, disposables)

        viewModel.state
            .distinctUntilChanged { t1, t2 ->
                t1.measurement?.temperature == t2.measurement?.temperature
            }
            .map {
                if (it.measurement?.temperature == null) "--.-"
                else {
                    val t = it.measurement.temperature / 10f
                    when {
                        t > 0 -> "+%.1f".format(t)
                        t < 0 -> "%.1f".format(t)
                        else -> "0°"
                    }

                }
            }
            .into(view.temperatureText, disposables)

        viewModel.state
            .distinctUntilChanged { t1, t2 ->
                t1.measurement?.temperature == t2.measurement?.temperature
            }
            .map {
                if (it.measurement?.temperature == null) TemperatureLevel.Unknown
                else when {
                    it.measurement.temperature < 207 -> TemperatureLevel.Cold
                    it.measurement.temperature >= 240 -> TemperatureLevel.Hot
                    else -> TemperatureLevel.Good
                }
            }
            .into(view.temperatureLevel, disposables)

        viewModel.state
            .map { it.loading == Loading.Visible }
            .distinctUntilChanged()
            .into(view.loadingVisibility, disposables)

        viewModel.state
            .distinctUntilChanged { t1, t2 ->
                t1.failure == t2.failure && t1.measurement == t2.measurement
            }
            .map {
                if (it.measurement != null) None
                else it.failure?.error.toOption()
            }
            .distinctUntilChanged()
            .into(view.messageText, disposables)
    }
}
