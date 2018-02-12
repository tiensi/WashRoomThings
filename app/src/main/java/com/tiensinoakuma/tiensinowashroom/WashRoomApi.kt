package com.tiensinoakuma.tiensinowashroom

import io.reactivex.Completable
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WashRoomApi {
    @PUT("room/{name}")
    fun updateRoom(@Path("name") name: String,
                   @Query("vacant") vacant: Boolean,
                   @Query("password") password: String): Completable
}