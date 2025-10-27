package com.example.alphakids

import android.app.Application
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AlphakidsApp : Application(), ImageLoaderFactory {
    
    @Inject
    lateinit var imageLoader: ImageLoader
    
    override fun onCreate() {
        super.onCreate()
        Log.d("AlphakidsApp", "Inicializando aplicación")
    }
    
    override fun newImageLoader(): ImageLoader {
        Log.d("AlphakidsApp", "Creando nuevo ImageLoader para Coil")
        return imageLoader
    }
}
