package com.example.fetch.main

import com.example.fetch.data.FetchHiringApi
import com.example.fetch.data.models.HiringResponse
import com.example.fetch.util.Resource
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(
    private val api: FetchHiringApi
) : MainRepository {
    override suspend fun getHiringData(): Resource<HiringResponse> {
        return try {
            val response = api.getHiringData()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error("Failed to fetch hiring data")
            }
        } catch(e: Exception) {
            Resource.Error(e.message ?: "An error occursed")
        }
    }
}