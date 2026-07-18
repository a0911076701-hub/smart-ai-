package com.yourcompany.appname.di

import com.yourcompany.appname.data.remote.HuggingFaceApi
import com.yourcompany.appname.data.remote.OpenAiCompatibleApi
import com.yourcompany.appname.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * وحدة حقن التبعيات (Hilt) الخاصة بطبقة الشبكة.
 * تقوم بإنشاء عميل OkHttp مشترك، ثم Retrofit منفصل لكل مزود API
 * لأن كل واحد منها له Base URL مختلف.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("groq")
    fun provideGroqApi(client: OkHttpClient): OpenAiCompatibleApi =
        Retrofit.Builder()
            .baseUrl(Constants.GROQ_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiCompatibleApi::class.java)

    @Provides
    @Singleton
    @Named("cerebras")
    fun provideCerebrasApi(client: OkHttpClient): OpenAiCompatibleApi =
        Retrofit.Builder()
            .baseUrl(Constants.CEREBRAS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiCompatibleApi::class.java)

    @Provides
    @Singleton
    @Named("openrouter")
    fun provideOpenRouterApi(client: OkHttpClient): OpenAiCompatibleApi =
        Retrofit.Builder()
            .baseUrl(Constants.OPENROUTER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiCompatibleApi::class.java)

    @Provides
    @Singleton
    fun provideHuggingFaceApi(client: OkHttpClient): HuggingFaceApi =
        Retrofit.Builder()
            .baseUrl(Constants.HUGGINGFACE_BASE_URL) // Base URL شكلي فقط، نستخدم @Url الكامل
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceApi::class.java)
}
