package com.johel.smartschoolapp.api

import retrofit2.Call
import retrofit2.http.*

interface RouteApiService {

    @GET("routes")
    fun getRoutes(): Call<List<RouteDto>>

    @POST("routes")
    fun createRoute(@Body dto: RouteDto): Call<RouteDto>

    @PUT("routes/{id}")
    fun updateRoute(
        @Path("id") id: String,
        @Body dto: RouteDto
    ): Call<RouteDto>

    @DELETE("routes/{id}")
    fun deleteRoute(@Path("id") id: String): Call<RouteDto>
}
