package com.example.core.di

import com.example.core.BuildConfig
import com.example.core.data.util.DateAdapter
import com.example.core.data.util.WebServiceClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() : Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if(BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE

        return interceptor
    }

    @Provides
    @Singleton
    fun provideDispatcher(): Dispatcher {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 10
        return dispatcher
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
            dispatcher: Dispatcher, httpLoggingInterceptor: Interceptor
    ) : OkHttpClient =
            OkHttpClient()
                    .newBuilder()
                    .dispatcher(dispatcher)
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()

    @Provides
    @Singleton
    fun provideMoshi() : Moshi =
            Moshi.Builder()
                .add(Date::class.java, DateAdapter().nullSafe())
                .build()


    @Provides
    @Singleton
    fun provideApiClient(client: OkHttpClient, moshi: Moshi) : WebServiceClient
    = WebServiceClient(client, moshi)
}