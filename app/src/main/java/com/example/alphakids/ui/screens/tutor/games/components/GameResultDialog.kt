package com.example.alphakids.ui.screens.tutor.games.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import com.example.alphakids.ui.components.ErrorButton
import com.example.alphakids.ui.components.ErrorTonalButton
import com.example.alphakids.ui.components.IconContainer
import com.example.alphakids.ui.components.LetterBox
import com.example.alphakids.ui.components.PrimaryButton
import com.example.alphakids.ui.components.PrimaryTonalButton
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

sealed class GameResultState {
    data class Success(
        val word: String,
        val imageIcon: ImageVector
    ) : GameResultState()

    data class Failure(
        val imageIcon: ImageVector
    ) : GameResultState()
}

@Composable
fun GameResultDialog(
    state: GameResultState,
    onDismiss: () -> Unit,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                when (state) {
                    is GameResultState.Success -> SuccessContent(
                        state = state,
                        onPrimaryAction = onPrimaryAction,
                        onSecondaryAction = onSecondaryAction
                    )
                    is GameResultState.Failure -> FailureContent(
                        state = state,
                        onPrimaryAction = onPrimaryAction,
                        onSecondaryAction = onSecondaryAction
                    )
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(
    state: GameResultState.Success,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SentimentSatisfied,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "¡Lo lograste!",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconContainer(
                icon = state.imageIcon,
                contentDescription = "Palabra"
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.word.forEach { char ->
                    LetterBox(
                        letter = char,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PrimaryButton(
                text = "Continuar",
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth()
            )
            PrimaryTonalButton(
                text = "Volver",
                onClick = onSecondaryAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FailureContent(
    state: GameResultState.Failure,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.SentimentVeryDissatisfied,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "¡Intento Fallido!",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconContainer(
                icon = state.imageIcon,
                contentDescription = "Palabra",
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Vuelve a intentarlo",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.error
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ErrorButton(
                text = "Seguir jugando",
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth()
            )
            ErrorTonalButton(
                text = "Salir",
                onClick = onSecondaryAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameResultDialogSuccessPreview() {
    AlphakidsTheme {
        GameResultDialog(
            state = GameResultState.Success(
                word = "GATO",
                imageIcon = Icons.Rounded.Checkroom
            ),
            onDismiss = {},
            onPrimaryAction = {},
            onSecondaryAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameResultDialogFailurePreview() {
    AlphakidsTheme {
        GameResultDialog(
            state = GameResultState.Failure(
                imageIcon = Icons.Rounded.Checkroom
            ),
            onDismiss = {},
            onPrimaryAction = {},
            onSecondaryAction = {}
        )
    }
}
