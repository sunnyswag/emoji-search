package com.example.emojisemanticsearch.ui.item

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.emoji_data_reader.processor.ProcessorFactory
import com.example.emojisemanticsearch.R
import com.example.emojisemanticsearch.utils.saveToClipboard
import com.example.model.EmojiInfoEntity

@Composable
fun DisplayEmoji(emojiData: List<EmojiInfoEntity>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(horizontal = 10.dp)) {
        items(emojiData.size) { index ->
            EmojiItem(emojiData[index])
        }
    }
}

@Composable
fun EmojiItem(emojiInfoEntity: EmojiInfoEntity) {
    val context = LocalContext.current
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            saveToClipboard(context, emojiInfoEntity.emoji)
            Toast
                .makeText(
                    context, "Copy ${emojiInfoEntity.emoji} to clipboard", Toast.LENGTH_SHORT
                )
                .show()
        }) {
        Text(
            text = emojiInfoEntity.emoji,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = emojiInfoEntity.message,
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
                "emojiData size: ${ProcessorFactory.emojiInfoData.size}"
            )
            onSearch(searchText.text)
        })
    )
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