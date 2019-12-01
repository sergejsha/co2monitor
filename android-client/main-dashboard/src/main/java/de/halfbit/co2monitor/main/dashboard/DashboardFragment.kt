package de.halfbit.co2monitor.main.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import de.halfbit.co2monitor.main.epi.MainFragmentFactory
import de.halfbit.co2monitor.main.mvi.MviFragment
import de.halfbit.co2monitor.main.utils.applyInsets
import de.halfbit.co2monitor.main.utils.consumeInsets
import magnet.Instance

internal class DashboardFragment : MviFragment<DashboardViewModel>() {

    override val viewModel = DashboardViewModel::class
    override val viewBinder = DashboardViewBinder::class
    override var layoutId: Int = R.layout.fragment_dashboard

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.applyInsets { it, insets, padding -> it.consumeInsets(padding, insets) }
    }
}

@Instance(type = MainFragmentFactory::class)
internal class DashboardFragmentFactory : MainFragmentFactory {
    override fun create(): Fragment = DashboardFragment()
}
