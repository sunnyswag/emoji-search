package com.example.emojisemanticsearch

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.emojisemanticsearch.entity.EmojiEntity
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiData
import com.example.emojisemanticsearch.startup.AppInitializer.Companion.emojiEmbeddings
import com.example.emojisemanticsearch.ui.theme.EmojiSemanticSearchTheme
import com.example.emojisemanticsearch.utils.saveToClipboard
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "emojiData size: ${emojiData.size}, emojiEmbeddings size: ${emojiEmbeddings.length}")
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
                        LaunchedEffect(key1 = true) {
                            viewModel.fetchSuccessAfter2s()
                        }

                        SearchEmoji(modifier = Modifier.fillMaxWidth())
                        if (uiState is UiState.Success) {
                            val uiState = uiState as UiState.Success
                            DisplayEmoji(
                                uiState.data,
                                modifier = Modifier.fillMaxWidth()
                            )
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
    Row(modifier = Modifier.clickable {
        saveToClipboard(context, emojiEntity.emoji)
        Toast.makeText(
            context,
            "Copied ${emojiEntity.emoji} to clipboard",
            Toast.LENGTH_SHORT
        ).show()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchEmoji(modifier: Modifier) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    TextField(
        value = searchText,
        placeholder = { Text("Find the most relevant emojis") },
        onValueChange = { searchText = it },
        modifier = modifier.padding(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                Log.d(
                    "MainActivity",
                    "emojiData size: ${emojiData.size}, emojiEmbeddings size: ${emojiEmbeddings.length}"
                )
                Toast.makeText(
                    context,
                    "Search for ${searchText.text}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchEmojiPreview() {
    EmojiSemanticSearchTheme {
        SearchEmoji(modifier = Modifier.fillMaxWidth())
    }
}