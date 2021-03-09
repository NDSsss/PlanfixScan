package ru.nds.planfix.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.nds.planfix.network.BarcodeParseApi

val networkModule = module {
    factory {
        GsonBuilder()
            .setLenient()
            .create()
    }
    factory {
        val gson: Gson = get()
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .baseUrl("https://barcode-list.ru/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .build()
    }
    factory {
        val retrofit: Retrofit = get()
        retrofit.create(BarcodeParseApi::class.java)
    }
}