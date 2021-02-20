package ru.nds.planfix.scan.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.nds.planfix.scan.data.BarcodeParseApi

object NetworkObjectsHolder {
    val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    val retrofit: Retrofit by lazy {
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

    val barcodeParseApi: BarcodeParseApi by lazy {
        retrofit.create(BarcodeParseApi::class.java)
    }
}