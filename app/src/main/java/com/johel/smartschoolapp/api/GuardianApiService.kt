package com.johel.smartschoolapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GuardianApiService {

    @GET("guardians")
    fun getGuardians(): Call<List<GuardianDto>>

    @GET("guardians/{id}")
    fun getGuardian(@Path("id") id: String): Call<GuardianDto>

    @POST("guardians")
    fun createGuardian(@Body guardian: GuardianDto): Call<GuardianDto>

    @PUT("guardians/{id}")
    fun updateGuardian(
        @Path("id") id: String,
        @Body guardian: GuardianDto
    ): Call<GuardianDto>

    @DELETE("guardians/{id}")
    fun deleteGuardian(@Path("id") id: String): Call<GuardianDto>
}
