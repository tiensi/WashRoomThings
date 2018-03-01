package com.tiensinoakuma.tiensinowashroom

import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface WashRoomApi {
    @PUT("washroom/{name}")
    fun updateRoom(@Path("name") name: String,
                   @Body amenityUpdateRequest: AmenityUpdateRequest): Completable
}