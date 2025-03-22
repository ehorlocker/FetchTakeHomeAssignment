package com.example.fetch

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fetch.databinding.ActivityMainBinding
import com.example.fetch.main.MainViewModel
import com.example.fetch.ui.adapters.HiringDataRecyclerViewAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.mainFetchButton.setOnClickListener {
            viewModel.getData()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dataRetrieval.collect { event ->
                    when (event) {
                        is MainViewModel.HiringEvent.Empty -> {
                            Log.d(TAG, "Hiring event is empty")
                        }
                        is MainViewModel.HiringEvent.Failure -> {
                            Snackbar.make(binding.root,
                                "Loading failed. Please try again.",
                                Snackbar.LENGTH_SHORT)
                                .show()
                            Log.e(TAG, event.errorText)
                            binding.hiringLoadingBar.isVisible = false
                        }
                        is MainViewModel.HiringEvent.Loading -> {
                            binding.hiringLoadingBar.isVisible = true
                        }
                        is MainViewModel.HiringEvent.Success -> {
                            val hiringResponseAdapter =
                                HiringDataRecyclerViewAdapter(event.resultData)

                            binding.hiringRecyclerView.layoutManager =
                                LinearLayoutManager(applicationContext)
                            binding.hiringRecyclerView.adapter = hiringResponseAdapter
                            binding.hiringLoadingBar.isVisible = false
                        }
                    }
                }
            }
        }
    }
}