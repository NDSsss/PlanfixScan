package ru.nds.planfix.network

import io.reactivex.rxjava3.core.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface BarcodeParseApi {
    @GET
    fun parseBarcode(@Url url: String, @Query("barcode") barcode: String): Single<ResponseBody>

    @Headers(
//        "Authorization: Basic MzBkNGEyNjYxNjc2NjBmZjExNzBmODFlYjA2YzZhZTM6NjQxNmI2MzI3MGM1YTU5YTI3YTIwZDBjN2ZiNjg0ZDk=",
        "Content-Type: application/xml"
    )
    @POST
    fun sendParsingToPlanFix(
        @Url url: String,
        @Header("Authorization") authHeader: String,
        @Body body: RequestBody
    ): Single<Response<ResponseBody>>
}