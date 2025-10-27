package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun ActionDialog(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    message: String,
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryButtonClick: (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    isError: Boolean = false
) {
    val iconContainerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
    val iconContentColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val messageColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 15.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                IconContainer(
                    icon = icon,
                    contentDescription = null,
                    containerColor = iconContainerColor,
                    contentColor = iconContentColor
                )

                Text(
                    text = message,
                    fontFamily = dmSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = messageColor,
                    textAlign = TextAlign.Center
                )

                if (secondaryButtonText != null && onSecondaryButtonClick != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SecondaryTonalButton(
                            text = secondaryButtonText,
                            onClick = onSecondaryButtonClick
                        )
                        if (isError) {
                            ErrorButton(
                                text = primaryButtonText,
                                onClick = onPrimaryButtonClick
                            )
                        } else {
                            PrimaryButton(
                                text = primaryButtonText,
                                onClick = onPrimaryButtonClick
                            )
                        }
                    }
                } else {
                    PrimaryButton(
                        text = primaryButtonText,
                        onClick = onPrimaryButtonClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


// --- PREVIEWS ---

@Preview(name = "Success Dialog", showBackground = true)
@Composable
fun ActionDialogSuccessPreview() {
    AlphakidsTheme {
        ActionDialog(
            icon = Icons.Rounded.CheckCircle,
            message = "Palabra editada exitosamente",
            primaryButtonText = "Aceptar",
            onPrimaryButtonClick = {},
            onDismissRequest = {},
            isError = false
        )
    }
}

@Preview(name = "Confirmation Dialog", showBackground = true)
@Composable
fun ActionDialogConfirmationPreview() {
    AlphakidsTheme {
        ActionDialog(
            icon = Icons.Rounded.Warning,
            message = "¿Estás seguro de eliminar esta palabra?",
            primaryButtonText = "Confirmar",
            onPrimaryButtonClick = {},
            secondaryButtonText = "Cancelar",
            onSecondaryButtonClick = {},
            onDismissRequest = {},
            isError = true
        )
    }
}
