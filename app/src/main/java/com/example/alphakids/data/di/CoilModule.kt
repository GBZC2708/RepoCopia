package com.example.alphakids.data.di

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import coil.util.Logger
import okio.Buffer
import okio.source
import okio.Source
import okio.buffer
import coil.decode.ImageSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.IOException
import java.net.URL
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {

    private const val TAG = "CoilModule"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .components {
                add(FirebaseStorageFetcher.Factory())
            }
            .logger(object : Logger {
                override fun log(tag: String, priority: Int, message: String?, throwable: Throwable?) {
                    when (priority) {
                        Log.ERROR -> Log.e(TAG, message ?: "", throwable)
                        Log.WARN -> Log.w(TAG, message ?: "", throwable)
                        Log.INFO -> Log.i(TAG, message ?: "", throwable)
                        Log.DEBUG -> Log.d(TAG, message ?: "", throwable)
                        Log.VERBOSE -> Log.v(TAG, message ?: "", throwable)
                        else -> Log.d(TAG, message ?: "", throwable)
                    }
                }

                override var level: Int = Log.DEBUG
            })
            .build()
    }
}

class FirebaseStorageFetcher(
    private val url: String,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        Log.d("FirebaseStorageFetcher", "Fetching URL: $url")

        return try {
            val cleanUrl = url.trim().replace("`", "")

            val connection = URL(cleanUrl).openConnection()
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()

            val inputStream = connection.getInputStream()

            Log.d("FirebaseStorageFetcher", "Fetch successful for URL: $cleanUrl")

            val bufferedSource = inputStream.source().buffer()
            SourceResult(
                source = ImageSource(bufferedSource, options.context),
                mimeType = connection.contentType,
                dataSource = DataSource.NETWORK
            )
        } catch (e: IOException) {
            Log.e("FirebaseStorageFetcher", "Error fetching URL: $url", e)
            throw e
        }
    }

    class Factory : Fetcher.Factory<String> {
        override fun create(data: String, options: Options, imageLoader: ImageLoader): Fetcher? {
            return if (data.contains("firebasestorage.googleapis.com")) {
                Log.d("FirebaseStorageFetcher", "Creating fetcher for Firebase Storage URL: $data")
                FirebaseStorageFetcher(data, options)
            } else {
                null
            }
        }
    }
}