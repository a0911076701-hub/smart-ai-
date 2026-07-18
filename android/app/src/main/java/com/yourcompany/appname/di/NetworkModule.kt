package com.yourcompany.appname.di

import com.yourcompany.appname.data.remote.CerebrasApi
import com.yourcompany.appname.data.remote.GroqApi
import com.yourcompany.appname.data.remote.HuggingFaceApi
import com.yourcompany.appname.data.remote.OpenRouterApi
import com.yourcompany.appname.data.remote.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGroqApi(): GroqApi = RetrofitClient.getGroqClient()

    @Provides
    @Singleton
    fun provideCerebrasApi(): CerebrasApi = RetrofitClient.getCerebrasClient()

    @Provides
    @Singleton
    fun provideOpenRouterApi(): OpenRouterApi = RetrofitClient.getOpenRouterClient()

    @Provides
    @Singleton
    fun provideHuggingFaceApi(): HuggingFaceApi = RetrofitClient.getHuggingFaceClient()
}
