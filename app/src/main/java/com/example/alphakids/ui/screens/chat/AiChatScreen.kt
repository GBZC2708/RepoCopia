package com.example.alphakids.ui.screens.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.AiMessageBubble
import com.example.alphakids.ui.components.AssignmentCard
import com.example.alphakids.ui.components.ChatHeader
import com.example.alphakids.ui.components.ChatInputBar
import com.example.alphakids.ui.theme.AlphakidsTheme

sealed class ChatMessage {
    data class Text(val content: String) : ChatMessage()
    data class Recommendation(val wordTitle: String, val wordSubtitle: String, val chipText: String) : ChatMessage()
}

@Composable
fun AiChatScreen(
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onAssignWord: (wordId: String) -> Unit
) {
    val messages = listOf(
        ChatMessage.Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce blandit luctus egestas. Fusce neque mauris."),
        ChatMessage.Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce blandit luctus egestas. Fusce neque mauris."),
        ChatMessage.Recommendation("WORD", "Categoría", "Chip")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            ChatHeader(
                modifier = Modifier.fillMaxWidth()
            )
        },
        bottomBar = {
            ChatInputBar(
                onSendClick = { onSendMessage("Nuevo mensaje") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages.size) { index ->
                    val message = messages[index]
                    when (message) {
                        is ChatMessage.Text -> {
                            AiMessageBubble(
                                message = message.content
                            )
                        }
                        is ChatMessage.Recommendation -> {
                            AiMessageBubble(
                                message = ""
                            ) {
                                AssignmentCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    wordTitle = message.wordTitle,
                                    wordSubtitle = message.wordSubtitle,
                                    chipText = message.chipText,
                                    onClickAssign = { onAssignWord("word_id") }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    AlphakidsTheme {
        AiChatScreen(
            onBackClick = {},
            onSendMessage = {},
            onAssignWord = {}
        )
    }
}
