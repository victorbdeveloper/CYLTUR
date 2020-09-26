package com.cyl.cyltur.api_connection

import com.cyl.cyltur.model.Mit
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("ficheros/cct/wtur/monumentos.json")
    fun getMonuments(): Call<Mit>

}