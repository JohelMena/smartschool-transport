package com.johel.smartschoolapp.api

import retrofit2.Call
import retrofit2.http.*

interface BusApiService {

    @GET("buses")
    fun getBuses(): Call<List<BusDto>>

    @POST("buses")
    fun createBus(@Body dto: BusDto): Call<BusDto>

    @PUT("buses/{id}")
    fun updateBus(
        @Path("id") id: String,
        @Body dto: BusDto
    ): Call<BusDto>

    @DELETE("buses/{id}")
    fun deleteBus(@Path("id") id: String): Call<BusDto>
}
