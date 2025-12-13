package com.johel.smartschoolapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Asegúrate de que esta URL sea EXACTAMENTE la de tu API en Render
    private const val BASE_URL = "https://smartschool-api-zh5d.onrender.com/"

    // Cliente Retrofit único para toda la app
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Servicios
    val studentService: StudentApiService =
        retrofit.create(StudentApiService::class.java)

    val driverService: DriverApiService =
        retrofit.create(DriverApiService::class.java)

    val busService: BusApiService =
        retrofit.create(BusApiService::class.java)

    val routeService: RouteApiService =
        retrofit.create(RouteApiService::class.java)

    val guardianService: GuardianApiService =
        retrofit.create(GuardianApiService::class.java)

}
