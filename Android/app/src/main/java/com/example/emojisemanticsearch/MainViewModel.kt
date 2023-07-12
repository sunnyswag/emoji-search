package com.example.emojisemanticsearch

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emojisemanticsearch.entity.EmojiInfoEntity
import com.example.emojisemanticsearch.network.EmbeddingRepository
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiInfoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class MainViewModel(
    private val embeddingRepository: EmbeddingRepository
): ViewModel() {
    private var _uiState = MutableStateFlow<UiState>(UiState.Default)
    val uiState get() = _uiState

    private var lastUserInput = ""

    fun searchEmojis(userInput: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            val result = embeddingRepository.getEmbedding(userInput)
            lastUserInput = userInput
            if (result.isSuccess) {
                result.getOrNull()?.let { indexes ->
                    _uiState.emit(UiState.Success(indexes.map { emojiInfoData[it] }))
                } ?: kotlin.run {
                    _uiState.emit(UiState.Error(R.string.computation_error))
                    Log.e(TAG, "searchEmojis: computation error")
                }
            } else {
                _uiState.emit(UiState.Error(R.string.network_error))
                Log.e(TAG, "searchEmojis: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun reSearchEmojis() {
        searchEmojis(lastUserInput)
    }

    fun initState() {
        _uiState.value = UiState.Default
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

sealed class UiState {
    object Loading: UiState()
    data class Success(val data: List<EmojiInfoEntity>): UiState()
    data class Error(@StringRes val message: Int): UiState()
    object Default: UiState()
}