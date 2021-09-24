package com.tobianoapps.shibeapi.di

import android.util.Log
import com.moczul.ok2curl.CurlInterceptor
import com.tobianoapps.shibeapi.list.repository.ShibeRepositoryImpl
import com.tobianoapps.shibeapi.ShibeViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object Koin {

    private const val TIMEOUT = 10000L

    val appModule = module {

        // Repo
        factory { ShibeRepositoryImpl() }

        // View Model
        viewModel { ShibeViewModel(get()) }

        // Coroutine Dispatcher
        factory { Dispatchers.IO }


        // Ktor Http Client
        single {
            HttpClient(OkHttp) {

                // Json
                install(JsonFeature) {
                    serializer = KotlinxSerializer(
                    )
                }

                // Timeout
                install(HttpTimeout) {
                    TIMEOUT.let {
                        requestTimeoutMillis = it
                        connectTimeoutMillis = it
                        socketTimeoutMillis = it
                    }
                }

                // Apply to All Requests
                defaultRequest {

                    parameter("count", 100)
                    parameter("urls", "true")
                    parameter("httpsUrls", "true")

                    // Content Type
                    if (this.method != HttpMethod.Get) contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }

                // Optional OkHttp Interceptors
                engine {
                    addInterceptor(CurlInterceptor { Log.v("Curl", it) })
                }
            }
        }
    }
}
