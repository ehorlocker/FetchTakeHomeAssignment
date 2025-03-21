package com.example.fetch.main

import com.example.fetch.data.models.HiringResponse
import com.example.fetch.util.Resource

interface MainRepository {
    suspend fun getHiringData() : Resource<HiringResponse>
}