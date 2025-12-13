package com.johel.smartschoolapp.api

import retrofit2.Call
import retrofit2.http.*

interface AttendanceApiService {

    @GET("attendance")
    fun getAttendance(): Call<List<AttendanceDto>>

    @GET("attendance/{id}")
    fun getAttendanceById(
        @Path("id") id: String
    ): Call<AttendanceDto>

    @POST("attendance")
    fun createAttendance(
        @Body dto: AttendanceDto
    ): Call<AttendanceDto>

    @PUT("attendance/{id}")
    fun updateAttendance(
        @Path("id") id: String,
        @Body dto: AttendanceDto
    ): Call<AttendanceDto>

    @DELETE("attendance/{id}")
    fun deleteAttendance(
        @Path("id") id: String
    ): Call<AttendanceDto>
}
