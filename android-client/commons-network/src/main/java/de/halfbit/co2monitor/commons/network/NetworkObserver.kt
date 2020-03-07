package de.halfbit.co2monitor.commons.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import de.halfbit.co2monitor.commons.APPLICATION
import io.reactivex.rxjava3.core.Observable
import magnet.Classifier
import magnet.Instance

interface NetworkObserver {
    val networkAvailable: Observable<Unit>
}

@Instance(type = NetworkObserver::class)
internal class DefaultNetworkObserver(
    @Classifier(APPLICATION) context: Context
) : NetworkObserver {

    private val request = NetworkRequest.Builder().build()
    private val connectivityManager = context
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val networkAvailable: Observable<Unit>
        get() = Observable.create { emitter ->
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    if (!emitter.isDisposed) emitter.onNext(Unit)
                }
            }
            connectivityManager.registerNetworkCallback(request, callback)
            emitter.setCancellable { connectivityManager.unregisterNetworkCallback(callback) }
        }
}