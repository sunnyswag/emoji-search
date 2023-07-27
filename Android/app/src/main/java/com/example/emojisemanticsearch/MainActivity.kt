package com.example.emojisemanticsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.example.emojisemanticsearch.ui.item.DefaultPage
import com.example.emojisemanticsearch.ui.item.DisplayEmoji
import com.example.emojisemanticsearch.ui.item.ErrorPage
import com.example.emojisemanticsearch.ui.item.LoadingPage
import com.example.emojisemanticsearch.ui.item.SearchEmojiField
import com.example.emojisemanticsearch.ui.theme.EmojiSemanticSearchTheme
import com.example.emojisemanticsearch.utils.limitLength
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmojiSemanticSearchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UiContent()
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalComposeUiApi::class)
    private fun UiContent() {
        Column(modifier = Modifier.fillMaxSize()) {
            val viewModel: MainViewModel = getViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val controller = LocalSoftwareKeyboardController.current

            LaunchedEffect(key1 = true) {
                viewModel.initState()
            }

            SearchEmojiField(modifier = Modifier.fillMaxWidth(),
                onSearch = {
                    viewModel.searchEmojis(it.limitLength())
                    controller?.hide()
                }, onClickDeleteAll = {
                    viewModel.initState()
                })
            ProcessUiState(uiState, viewModel, controller)
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun ProcessUiState(
        uiState: UiState,
        viewModel: MainViewModel,
        controller: SoftwareKeyboardController?
    ) {
        val modifier = Modifier.fillMaxSize()
        when (uiState) {
            is UiState.Loading -> {
                LoadingPage(modifier)
            }

            is UiState.Success -> {
                DisplayEmoji(uiState.data, modifier = modifier)
            }

            is UiState.Error -> {
                ErrorPage(modifier, uiState.message) {
                    viewModel.reSearchEmojis()
                    controller?.hide()
                }
            }

            is UiState.Default -> {
                DefaultPage(modifier)
                controller?.show()
            }
        }
    }
}