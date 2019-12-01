package de.halfbit.co2monitor.main.magnet

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import de.halfbit.co2monitor.commons.ACTIVITY
import magnet.Scope
import magnet.bind

abstract class MagnetActivity : AppCompatActivity() {
    val scope: Scope by lazy {
        getAppScope()
            .createSubscope()
            .bind(this as FragmentActivity)
            .bind(this as LifecycleOwner)
            .bind(this as Context)
            .bind(layoutInflater)
            .bind(resources)
            .bind(supportFragmentManager)
            .limit(ACTIVITY)
            .disposeOnDestroy(this)
    }
}

fun Context.requireActivityScope(): Scope {
    if (this is MagnetActivity) {
        return this.scope
    }
    var context = this
    while (context is ContextWrapper) {
        context = context.baseContext
        if (context is MagnetActivity) {
            return context.scope
        }
    }
    error("Cannot find activity scope in context of $this")
}

fun Scope.disposeOnDestroy(lifecycleOwner: LifecycleOwner): Scope =
    AutoDisposableScope(scope = this, lifecycle = lifecycleOwner.lifecycle)

private class AutoDisposableScope(
    private val scope: Scope,
    private val lifecycle: Lifecycle
) : LifecycleEventObserver, Scope by scope {

    init {
        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            lifecycle.removeObserver(this)
            scope.dispose()
        }
    }
}
