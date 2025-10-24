package com.example.alphakids.ui.screens.tutor.games.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.InfoChip
import com.example.alphakids.ui.components.LetterBox
import com.example.alphakids.ui.components.PrimaryIconButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun WordPuzzleCard(
    modifier: Modifier = Modifier,
    wordLength: Int,
    icon: ImageVector,
    difficulty: String,
    onTakePhotoClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconContainer(
            icon = icon,
            contentDescription = "Pista"
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "¿   Qué es esto?",
            fontFamily = dmSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(wordLength) {
                LetterBox(
                    letter = null,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        PrimaryIconButton(
            icon = Icons.Rounded.CameraAlt,
            contentDescription = "Tomar foto",
            onClick = onTakePhotoClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoChip(
            text = difficulty,
            isSelected = true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun WordPuzzleCardPreview() {
    AlphakidsTheme {
        WordPuzzleCard(
            modifier = Modifier.padding(16.dp),
            wordLength = 4,
            icon = Icons.Rounded.Checkroom,
            difficulty = "Difícil",
            onTakePhotoClick = {}
        )
    }
}
