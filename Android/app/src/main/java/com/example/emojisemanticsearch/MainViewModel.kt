package com.example.emojisemanticsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emojisemanticsearch.entity.EmojiEntity
import com.example.emojisemanticsearch.network.EmbeddingRepository
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


    fun searchEmojis(userInput: String = "I love you") {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            embeddingRepository.getEmbedding(userInput)
            _uiState.emit(UiState.Success(testData(), "result"))
        }
    }

    private fun testData() = listOf(
        EmojiEntity("🐶", "Dog"),
        EmojiEntity("🐱", "Cat"),
        EmojiEntity("🐭", "Mouse"),
        EmojiEntity("🐹", "Hamster"),
        EmojiEntity("🐰", "Rabbit"),
        EmojiEntity("🦊", "Fox"),
        EmojiEntity("🐻", "Bear"),
        EmojiEntity("🐼", "Panda"),
        EmojiEntity("🐻‍❄️", "Polar Bear"),
        EmojiEntity("🐶", "Dog"),
        EmojiEntity("🐱", "Cat"),
        EmojiEntity("🐭", "Mouse"),
        EmojiEntity("🐹", "Hamster"),
        EmojiEntity("🐰", "Rabbit"),
        EmojiEntity("🦊", "Fox"),
        EmojiEntity("🐻", "Bear"),
        EmojiEntity("🐼", "Panda"),
        EmojiEntity("🐻‍❄️", "Polar Bear"),
        EmojiEntity("🐶", "Dog"),
        EmojiEntity("🐱", "Cat"),
        EmojiEntity("🐭", "Mouse"),
        EmojiEntity("🐹", "Hamster"),
        EmojiEntity("🐰", "Rabbit"),
        EmojiEntity("🦊", "Fox"),
        EmojiEntity("🐻", "Bear"),
        EmojiEntity("🐼", "Panda"),
        EmojiEntity("🐻‍❄️", "Polar Bear")
    )
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}

sealed class UiState {
    object Loading: UiState()
    data class Success(val data: List<EmojiEntity>, val embeddingTest: String): UiState()
    data class Error(val message: String): UiState()
    object Default: UiState()
}