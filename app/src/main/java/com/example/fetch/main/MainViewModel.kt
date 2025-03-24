package com.example.fetch.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fetch.data.models.HiringResponseItem
import com.example.fetch.util.DispatcherProvider
import com.example.fetch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private lateinit var hiringDataCache : List<HiringResponseItem>
    private var isListIdSortAscending = true
    private var isNameSortAscending = true
    private val _dataRetrieval = MutableStateFlow<HiringEvent>(HiringEvent.Empty)
    val dataRetrieval: StateFlow<HiringEvent> = _dataRetrieval.asStateFlow()


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
                    hiringDataCache = hiringResponse.data!!
                        .filter{ !it.name.isNullOrBlank() }
                    sortHiringData()
                }
            }
        }
    }

    fun sortHiringData() {
        if (!::hiringDataCache.isInitialized) {
            return
        }
        val listIdOrder = if (isListIdSortAscending) 1 else -1
        val nameOrder = if (isNameSortAscending) 1 else -1

        val sortedHiringData = hiringDataCache
            .sortedWith(
                compareBy<HiringResponseItem> {it.listId * listIdOrder}
                    .thenComparator { a, b ->
                        val firstComparison = a.name.substringBefore(' ')
                            .compareTo(b.name.substringBefore(' ')) * nameOrder
                        if (firstComparison != 0) return@thenComparator firstComparison

                        val secondComparison = a.name.substringAfter(' ').toInt()
                            .compareTo(b.name.substringAfter(' ').toInt()) * nameOrder
                        return@thenComparator secondComparison
                    })
        _dataRetrieval.value = HiringEvent.Success(sortedHiringData)
    }

    fun flipListIdSortAscending() {
        isListIdSortAscending = !isListIdSortAscending
    }

    fun flipNameSortAscending() {
        isNameSortAscending = !isNameSortAscending
    }

    fun isListIdSortAscending() : Boolean {
        return isListIdSortAscending
    }

    fun isNameSortAscending() : Boolean {
        return isNameSortAscending
    }
}