package com.example.alphakids.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun AlphaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    // Parámetro para manejar campos de contraseña
    visualTransformation: VisualTransformation = VisualTransformation.None,
    // Parámetro para configurar el tipo de teclado
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp), // Esquinas redondeadas
            placeholder = { Text(text = placeholderText) },
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            // Aquí personalizamos los colores para que coincidan con tu diseño
            colors = TextFieldDefaults.colors(
                // Color de fondo cuando el campo está activo o inactivo
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                // Color del texto y del cursor
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                // Hacemos transparente la línea indicadora de abajo
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun AlphaTextFieldPreview() {
    AlphakidsTheme {
        // Usamos 'remember' para que la preview sea interactiva
        var emailValue by remember { mutableStateOf("") }
        var passwordValue by remember { mutableStateOf("12345") }

        Column(modifier = Modifier.padding(16.dp)) {
            AlphaTextField(
                value = emailValue,
                onValueChange = { emailValue = it },
                label = "Correo Electrónico",
                placeholderText = "tutor@email.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AlphaTextField(
                value = passwordValue,
                onValueChange = { passwordValue = it },
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation() // Oculta el texto
            )
        }
    }
}