package com.yourcompany.appname.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface HuggingFaceApi {
    @Headers("Content-Type: application/json")
    @POST
    suspend fun generate(@Url modelUrl: String, @Body request: HFRequest): List<HFResponseItem>
}
