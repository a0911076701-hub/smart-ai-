package com.yourcompany.appname.di

import com.yourcompany.appname.data.remote.CerebrasApi
import com.yourcompany.appname.data.remote.GroqApi
import com.yourcompany.appname.data.remote.HuggingFaceApi
import com.yourcompany.appname.data.remote.OpenRouterApi
import com.yourcompany.appname.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private fun authInterceptor(token: String): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        chain.proceed(request)
    }

    private fun buildClient(token: String): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC // لا نسجّل المحتوى الكامل لتفادي تسريب البيانات في السجلات
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor(token))
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @Named("groq")
    fun provideGroqRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.GROQ_BASE_URL)
        .client(buildClient(Constants.GROQ_API_KEY))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideGroqApi(@Named("groq") retrofit: Retrofit): GroqApi = retrofit.create(GroqApi::class.java)

    @Provides
    @Singleton
    @Named("cerebras")
    fun provideCerebrasRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.CEREBRAS_BASE_URL)
        .client(buildClient(Constants.CEREBRAS_API_KEY))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideCerebrasApi(@Named("cerebras") retrofit: Retrofit): CerebrasApi =
        retrofit.create(CerebrasApi::class.java)

    @Provides
    @Singleton
    @Named("openrouter")
    fun provideOpenRouterRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.OPENROUTER_BASE_URL)
        .client(buildClient(Constants.OPENROUTER_API_KEY))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideOpenRouterApi(@Named("openrouter") retrofit: Retrofit): OpenRouterApi =
        retrofit.create(OpenRouterApi::class.java)

    @Provides
    @Singleton
    @Named("huggingface")
    fun provideHFRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.HF_BASE_URL)
        .client(buildClient(Constants.HF_API_KEY))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideHFApi(@Named("huggingface") retrofit: Retrofit): HuggingFaceApi =
        retrofit.create(HuggingFaceApi::class.java)
}
