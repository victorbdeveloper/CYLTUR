package com.cyl.cyltur.api_connection

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtils {

    val api: ApiService = Retrofit.Builder()
        .baseUrl("https://opendata.jcyl.es/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ApiService::class.java)

}