package com.example.petfinder.data

import android.content.Context
import com.example.petfinder.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RestClient {
    private lateinit var apiService: PetfinderService

    fun getApiService(context: Context): PetfinderService {

        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient(context))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

            apiService = retrofit.create(PetfinderService::class.java)
        }

        return apiService
    }

    /**
     * Initialize OkhttpClient with our interceptor
     */
    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }
}