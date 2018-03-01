package com.tiensinoakuma.tiensinowashroom

import com.google.gson.annotations.SerializedName

data class AmenityUpdateRequest(@SerializedName("amenity_name") val amenityName: String, var status: String, val password: String)