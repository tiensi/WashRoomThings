package com.tiensinoakuma.tiensinowashroom

import io.reactivex.Completable
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WashRoomApi {
    @PUT("washroom/{name}")
    fun updateRoom(@Path("name") name: String,
                   @Query("status") status: String,
                   @Query("password") password: String): Completable
}