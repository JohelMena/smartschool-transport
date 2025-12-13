package com.johel.smartschoolapp.api

import retrofit2.Call
import retrofit2.http.*

interface StudentApiService {

    @GET("students")
    fun getStudents(): Call<List<StudentDto>>

    @POST("students")
    fun createStudent(@Body student: StudentDto): Call<StudentDto>

    @PUT("students/{id}")
    fun updateStudent(
        @Path("id") id: String,
        @Body student: StudentDto
    ): Call<StudentDto>

    @DELETE("students/{id}")
    fun deleteStudent(@Path("id") id: String): Call<StudentDto>
}
