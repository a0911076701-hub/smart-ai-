package com.yourcompany.appname.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/** واجهة Cerebras API - متوافقة مع صيغة OpenAI Chat Completions */
interface CerebrasApi {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResponse
}
