package ru.nds.planfix.scan.data

import okhttp3.Interceptor
import okhttp3.Response

class AuthTokenInterceptor(
    private val prefsStorage: IPrefsStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.request().newBuilder()
            .apply {
//                if(chain.request().url)
                header("Authorization", "Base ${prefsStorage.authHeader}")
            }
            .build().let { chain.proceed(it) }
    }
}