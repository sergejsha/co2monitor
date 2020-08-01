package de.halfbit.co2monitor.repo.measurement.graphql

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import de.halfbit.co2monitor.commons.Option
import de.halfbit.co2monitor.commons.toOption
import de.halfbit.co2monitor.main.dashboard.graphql.MeasurementQuery
import de.halfbit.co2monitor.repo.measurement.Measurement
import de.halfbit.co2monitor.repo.measurement.MeasurementRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import magnet.Instance
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import org.threeten.bp.Instant
import org.threeten.bp.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Instance(type = MeasurementRepository::class)
internal class GraphqlMeasurementRepository(
    private val graphqlClient: ApolloClient
) : MeasurementRepository {

    override fun getCurrentMeasurement(): Single<Option<Measurement>> =
        Single
            .create { emitter: SingleEmitter<Option<Measurement>> ->
                graphqlClient.query(MeasurementQuery()).let { call ->
                    call.enqueue(object : ApolloCall.Callback<MeasurementQuery.Data>() {
                        override fun onFailure(e: ApolloException) {
                            e.printStackTrace()
                            emitter.tryOnError(e)
                        }

                        override fun onResponse(response: Response<MeasurementQuery.Data>) {
                            if (!emitter.isDisposed) {
                                val current = response.data()?.current
                                emitter.onSuccess(current?.toMeasurement().toOption())
                            }
                        }
                    })
                    emitter.setCancellable { call.cancel() }
                }
            }
            .subscribeOn(Schedulers.io())
}

private fun MeasurementQuery.Current.toMeasurement(): Measurement =
    Measurement(
        time = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(time, Instant.FROM),
        co2 = co2,
        temperature = temperature
    )

@Instance(type = OkHttpClient::class)
internal fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

@Instance(type = ApolloClient::class)
internal fun provideApolloClient(httpClient: OkHttpClient): ApolloClient =
    ApolloClient
        .builder()
        .serverUrl("http://192.168.178.28:8080/graphql")
        .callFactory(
            object : Call.Factory {
                override fun newCall(request: Request): Call =
                    httpClient.newCall(request)
            }
        )
        .build()