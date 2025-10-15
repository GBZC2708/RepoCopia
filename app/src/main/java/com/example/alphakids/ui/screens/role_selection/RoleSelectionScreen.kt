package com.example.alphakids.ui.screens.role_selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.RoleCard
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun RoleSelectionScreen(

    onRoleSelected: (role: String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize() //
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Encabezado ---
        Text(
            text = "¿Qué tipo de Usuario eres?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Selecciona una opción para continuar",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // --- Tarjeta de Tutor ---
        RoleCard(
            rol = "Tutor",
            description = "Si quieres dar seguimiento al aprendizaje de tus hijos",
            icon = Icons.Rounded.Face,
            onClick = { onRoleSelected("Tutor") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        RoleCard(
            rol = "Docente",
            description = "Si quieres gestionar el aprendizaje de tus estudiantes",
            icon = Icons.Rounded.School,
            onClick = { onRoleSelected("Docente") }
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun RoleSelectionScreenPreview() {
    AlphakidsTheme {
        // Para la preview, simplemente le pasamos una función vacía al callback
        RoleSelectionScreen(onRoleSelected = {})
    }
}