package com.example.fetch.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetch.data.models.HiringResponseItem
import com.example.fetch.util.DispatcherProvider
import com.example.fetch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatchers: DispatcherProvider
): ViewModel() {
    sealed class HiringEvent {
        class Success(val resultData: List<HiringResponseItem>) : HiringEvent()
        class Failure(val errorText: String) : HiringEvent()
        object Loading : HiringEvent()
        object Empty : HiringEvent()
    }

    private val _dataRetrieval = MutableStateFlow<HiringEvent>(HiringEvent.Empty)
    val dataRetrieval: StateFlow<HiringEvent> = _dataRetrieval

    fun getData () {
        viewModelScope.launch(dispatchers.io) {
            _dataRetrieval.value = HiringEvent.Loading
            when (val hiringResponse = repository.getHiringData()) {
                is Resource.Error -> _dataRetrieval.value =
                    HiringEvent.Failure(hiringResponse.message!!)
                is Resource.Success -> {
                    if (hiringResponse.data == null) {
                        HiringEvent.Failure("Failed to retrieve hiring data")
                    }
                    val filteredHiringData = hiringResponse.data!!
                        .filter{ !it.name.isNullOrBlank() }
                    val sortedHiringData = filteredHiringData
                        .sortedWith(compareBy({it.listId},
                            { it.name.substring(0, it.name.indexOf(' ')) },
                            { it.name.substring(it.name.indexOf(' ') + 1).toInt() }))
                    _dataRetrieval.value = HiringEvent.Success(sortedHiringData)
                }
            }
        }
    }
}