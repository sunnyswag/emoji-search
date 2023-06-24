package com.example.emojisemanticsearch

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.emojisemanticsearch.entity.EmojiEntity
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiData
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiEmbeddings
import com.example.emojisemanticsearch.ui.theme.EmojiSemanticSearchTheme
import com.example.emojisemanticsearch.utils.saveToClipboard
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "emojiData size: ${emojiData.size}, emojiEmbeddings size: ${emojiEmbeddings.shape}")
        setContent {
            EmojiSemanticSearchTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        val viewModel: MainViewModel = getViewModel()
                        val uiState by viewModel.uiState.collectAsState()
                        val controller = LocalSoftwareKeyboardController.current

                        LaunchedEffect(key1 = true) {
                            viewModel.initState()
                        }

                        SearchEmojiField(modifier = Modifier.fillMaxWidth(),
                            onSearch = {
                                viewModel.searchEmojis(it)
                                controller?.hide()
                            }, onClickDeleteAll = {
                                viewModel.initState()
                            })
                        val modifier = Modifier.fillMaxSize()
                        when (uiState) {
                            is UiState.Loading -> {
                                LoadingPage(modifier)
                            }
                            is UiState.Success -> {
                                val state = uiState as UiState.Success
                                DisplayEmoji(state.data, modifier = modifier)
                            }
                            is UiState.Error -> {
                                val state = uiState as UiState.Error
                                ErrorPage(modifier, state.message) {
                                    viewModel.reSearchEmojis()
                                    controller?.hide()
                                }
                            }
                            is UiState.Default -> {
                                DefaultPage(modifier)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun DisplayEmoji(emojiData: List<EmojiEntity>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(horizontal = 10.dp)) {
        items(emojiData.size) { index ->
            EmojiItem(emojiData[index])
        }
    }
}

@Composable
fun EmojiItem(emojiEntity: EmojiEntity) {
    val context = LocalContext.current
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            saveToClipboard(context, emojiEntity.emoji)
            Toast
                .makeText(
                    context, "Copy ${emojiEntity.emoji} to clipboard", Toast.LENGTH_SHORT
                )
                .show()
        }) {
        Text(
            text = emojiEntity.emoji,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = emojiEntity.message,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SearchEmojiField(
    modifier: Modifier,
    onSearch: (String) -> Unit = {},
    onClickDeleteAll: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    TextField(value = searchText,
        placeholder = { Text("Find the most relevant emojis") },
        onValueChange = { searchText = it },
        leadingIcon = {
            Image(
                painterResource(id = R.drawable.search),
                contentDescription = "search",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        },
        trailingIcon = {
            AnimatedVisibility (
                visible = searchText.text.isNotEmpty(),
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                Image(
                    painterResource(id = R.drawable.delete_all_text),
                    contentDescription = "delete_text",
                    modifier = Modifier.clickable {
                        searchText = TextFieldValue("")
                        onClickDeleteAll()
                    },
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
        },
        modifier = modifier.padding(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            Log.d(
                "MainActivity",
                "emojiData size: ${emojiData.size}, emojiEmbeddings size: ${emojiEmbeddings.shape}"
            )
            onSearch(searchText.text)
        }))
}

@Composable
fun ErrorPage(
    modifier: Modifier = Modifier,
    @StringRes message: Int,
    clickErrorPage: () -> Unit = {}
) {
    Box(
        modifier = modifier.clickable { clickErrorPage() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = message),
        )
    }
}

@Composable
fun DefaultPage(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = "Search for emojis")
    }
}

@Composable
fun LoadingPage(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}