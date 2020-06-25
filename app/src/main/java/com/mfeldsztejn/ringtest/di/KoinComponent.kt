package com.mfeldsztejn.ringtest.di

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import com.mfeldsztejn.ringtest.PreferencesManager
import com.mfeldsztejn.ringtest.Storage
import com.mfeldsztejn.ringtest.data.source.PostsRepository
import com.mfeldsztejn.ringtest.data.source.PostsRepositoryImpl
import com.mfeldsztejn.ringtest.data.source.local.PostsDatabase
import com.mfeldsztejn.ringtest.data.source.local.PostsLocalDataSource
import com.mfeldsztejn.ringtest.data.source.remote.PostsRemoteDataSource
import com.mfeldsztejn.ringtest.data.source.remote.RedditAPI
import com.mfeldsztejn.ringtest.ui.detail.DetailViewModel
import com.mfeldsztejn.ringtest.ui.main.ListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

fun initializeKoin(application: Application) {
    startKoin {
        androidContext(application)
        modules(networkModule, databaseModule, repositoriesModule, viewModelModule)
    }
}

val networkModule = module {
    single { buildClient() }
    single { buildRetrofit(client = get()) }
    single { buildApi(retrofit = get()) }
    single { PostsRemoteDataSource(api = get()) }
}

private fun buildClient() = OkHttpClient.Builder().run {
    addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BASIC) })
    build()
}

private fun buildRetrofit(client: OkHttpClient) = Retrofit.Builder().run {
    baseUrl("https://www.reddit.com/")
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
            .fallbackToDestructiveMigration()
            .build()
    }
    single { PostsLocalDataSource(database = get()) }
}

val repositoriesModule = module {
    single {
        PostsRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get()
        ) as PostsRepository
    }
    single { PreferencesManager(get()) as Storage }
}

val viewModelModule = module {
    viewModel { (handle: SavedStateHandle) ->
        ListViewModel(
            storage = get(),
            repository = get(),
            savedState = handle
        )
    }
    viewModel { (id: Int) -> DetailViewModel(postId = id, postsRepository = get()) }
}