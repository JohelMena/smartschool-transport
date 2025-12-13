package com.johel.smartschoolapp.api

import retrofit2.Call
import retrofit2.http.*

interface DriverApiService {

    @GET("drivers")
    fun getDrivers(): Call<List<DriverDto>>

    @POST("drivers")
    fun createDriver(@Body driver: DriverDto): Call<DriverDto>

    @PUT("drivers/{id}")
    fun updateDriver(
        @Path("id") id: String,
        @Body driver: DriverDto
    ): Call<DriverDto>

    @DELETE("drivers/{id}")
    fun deleteDriver(@Path("id") id: String): Call<DriverDto>
}

