package de.halfbit.co2monitor.main.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign

interface ViewBinder {
    fun bind(disposables: CompositeDisposable)
    fun attach(lifecycleOwner: LifecycleOwner)
}

abstract class LifecycleViewBinder : ViewBinder {

    private val disposables = CompositeDisposable()
    private val observer = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (event) {
                Lifecycle.Event.ON_START -> bind(disposables)
                Lifecycle.Event.ON_STOP -> disposables.clear()
                Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)
            }
        }
    }

    final override fun attach(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    protected fun <T : Any> Observable<T>.into(
        consumer: Consumer<T>,
        disposables: CompositeDisposable
    ) {
        disposables +=
            observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
    }
}
