package com.example.fetch.data

import com.example.fetch.data.models.HiringResponse
import retrofit2.Response
import retrofit2.http.GET

interface FetchHiringApi {
    @GET("hiring.json")
    suspend fun getHiringData() : Response<HiringResponse>
}