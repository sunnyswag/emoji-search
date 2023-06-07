package com.example.emojisemanticsearch.network

import org.koin.dsl.module

val networkModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideOpenAIAPI(get()) }
}

val repositoryModule = module {
    single { EmbeddingRepository(get()) }
}