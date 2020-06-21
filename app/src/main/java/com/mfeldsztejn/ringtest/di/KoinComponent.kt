package com.mfeldsztejn.ringtest.di

import android.app.Application
import androidx.room.Room
import com.mfeldsztejn.ringtest.data.source.PostsRepositoryImpl
import com.mfeldsztejn.ringtest.data.source.local.PostsDatabase
import com.mfeldsztejn.ringtest.data.source.local.PostsLocalDataSource
import com.mfeldsztejn.ringtest.data.source.remote.PostsRemoteDataSource
import com.mfeldsztejn.ringtest.data.source.remote.RedditAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

fun initializeKoin(application: Application) {
    startKoin {
        androidContext(application)
        modules(networkModule, databaseModule, repositoriesModule)
    }
}

val networkModule = module {
    single { buildClient() }
    single { buildRetrofit(client = get()) }
    single { buildApi(retrofit = get()) }
    single { PostsRemoteDataSource(api = get()) }
}

private fun buildClient() = OkHttpClient.Builder().run {
    addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
    build()
}

private fun buildRetrofit(client: OkHttpClient) = Retrofit.Builder().run {
    baseUrl("")
    client(client)
    addConverterFactory(GsonConverterFactory.create())
    build()
}

private fun buildApi(retrofit: Retrofit) = retrofit.create<RedditAPI>()

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            PostsDatabase::class.java,
            "posts_database.db"
        )
    }
    single { PostsLocalDataSource(database = get()) }
}

val repositoriesModule = module {
    single { PostsRepositoryImpl(remoteDataSource = get(), localDataSource = get()) }
}