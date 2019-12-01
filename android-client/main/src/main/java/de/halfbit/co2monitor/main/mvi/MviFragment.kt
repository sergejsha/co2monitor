package de.halfbit.co2monitor.main.mvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.halfbit.co2monitor.main.magnet.MagnetFragment
import de.halfbit.co2monitor.main.magnet.getAppScope
import magnet.Scope
import kotlin.reflect.KClass

const val RETAINED = "retained"

abstract class MviFragment<V : Any> : MagnetFragment() {

    protected abstract val viewModel: KClass<V>
    protected abstract val viewBinder: KClass<out ViewBinder>
    protected abstract var layoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId, container, false)

    override fun onScopeCreated(scope: Scope, savedInstanceState: Bundle?) {
        val factory = ScopeFactory(requireContext().getAppScope())
        val provider = ViewModelProvider(this, factory)
        val retainedViewModel = provider.get(RetainedViewModel::class.java)
        val viewModelInstance = retainedViewModel.scope.getSingle(viewModel.java)
        scope.bind(viewModel.java, viewModelInstance)
        scope.getSingle(viewBinder.java).attach(this)
    }
}

private class RetainedViewModel(val scope: Scope) : ViewModel() {
    override fun onCleared() {
        scope.dispose()
    }
}

@Suppress("UNCHECKED_CAST")
private class ScopeFactory(private val appScope: Scope) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        check(modelClass == RetainedViewModel::class.java)
        return RetainedViewModel(appScope.createSubscope().limit(RETAINED)) as T
    }
}