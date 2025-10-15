package com.example.alphakids.ui.screens.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.AlphaPrimaryButton
import com.example.alphakids.ui.components.AlphaSecondaryButton
import com.example.alphakids.ui.components.GameImageContainer
import com.example.alphakids.ui.components.LetterInputBox
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun SuccessResultScreen(
    word: String,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = Icons.Rounded.SentimentSatisfied,
                contentDescription = "Éxito",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text("¡Lo lograste!", style = MaterialTheme.typography.headlineLarge)
            GameImageContainer(icon = Icons.Outlined.Checkroom)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                word.forEach { letter ->
                    LetterInputBox(letter = letter)
                }
            }
            AlphaPrimaryButton(text = "Continuar", onClick = onContinue)
            AlphaSecondaryButton(text = "Volver", onClick = onBack)
        }
    }
}

@Preview
@Composable
fun SuccessResultScreenPreview() {
    AlphakidsTheme {
        SuccessResultScreen("GATO", {}, {})
    }
}