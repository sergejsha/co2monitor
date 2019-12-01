package de.halfbit.co2monitor.main.magnet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import magnet.Scope
import magnet.bind

abstract class MagnetFragment : Fragment() {

    internal var scope: Scope? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireParentScope()
            .createSubscope()
            .bind<Fragment>(this@MagnetFragment)
            .bind(viewLifecycleOwner)
            .bind(this@MagnetFragment.childFragmentManager)
            .bind(view)
            .apply {
                scope = this
                onScopeCreated(this, savedInstanceState)
            }
    }

    override fun onDestroyView() {
        onDestroyScope()
        scope?.let {
            it.dispose()
            scope = null
        }
        super.onDestroyView()
    }

    protected open fun onScopeCreated(scope: Scope, savedInstanceState: Bundle?) {}
    protected open fun onDestroyScope() {}
}

private fun Fragment.requireParentScope(): Scope {
    parentFragment?.let { parentFragment ->
        if (parentFragment is MagnetFragment) {
            return checkNotNull(parentFragment.scope)
        }
    }
    return checkNotNull(context).requireActivityScope()
}
