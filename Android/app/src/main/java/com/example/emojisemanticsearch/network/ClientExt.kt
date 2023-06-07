package com.example.emojisemanticsearch.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun provideOpenAIAPI(retrofit: Retrofit): OpenAIAPI = retrofit.create(OpenAIAPI::class.java)

const val baseUrl = "https://api.openai.com/v1/"