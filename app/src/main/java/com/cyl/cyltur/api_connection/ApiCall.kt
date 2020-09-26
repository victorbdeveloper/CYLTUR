package com.cyl.cyltur.api_connection

import com.cyl.cyltur.model.Mit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiCall {

    companion object {

        fun getMonuments(listenerOk: (Mit?) -> Unit, listenerError: (Int) -> Unit) {

            ApiUtils.api.getMonuments().enqueue(object : Callback<Mit> {
                override fun onResponse(call: Call<Mit>, response: Response<Mit>) {

                    if (response.isSuccessful) {
                        val mit = response.body()
                        listenerOk(mit)
                    } else {
                        listenerError(response.code())
                    }

                }

                override fun onFailure(call: Call<Mit>, t: Throwable) {
                    t.printStackTrace()
                    listenerError(500)
                }
            })

        }

    }

}