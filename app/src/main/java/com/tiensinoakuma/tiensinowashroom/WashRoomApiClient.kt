package com.tiensinoakuma.tiensinowashroom;

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object WashRoomApiClient {

    private val api: WashRoomApi

    //todo fix api
    fun updateRoom(name: String, amenityName: String, vacant: Boolean): Completable {
        return api.updateRoom(name, AmenityUpdateRequest(amenityName, if (vacant) "Vacant" else "In Use", BuildConfig.WASHROOM_PASSWORD))
                .subscribeOn(Schedulers.io())
    }

    init {
        val client = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) BODY else NONE
                })
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.WASHROOM_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        api = retrofit.create(WashRoomApi::class.java)
    }
}
