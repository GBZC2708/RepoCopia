package com.example.alphakids.ui.screens.common

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphakids.ui.screens.common.components.RoleSelectionCard
import com.example.alphakids.ui.theme.AlphakidsTheme
import com.example.alphakids.ui.theme.dmSansFamily

@Composable
fun RoleSelectScreen(
    onTutorClick: () -> Unit,
    onTeacherClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "¿Qué tipo de Usuario eres?",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Selecciona una opción para continuar",
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            RoleSelectionCard(
                title = "Tutor",
                description = "Si quieres dar seguimiento al aprendizaje de tus hijos",
                icon = Icons.Rounded.Face,
                onClick = onTutorClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleSelectionCard(
                title = "Docente",
                description = "Si quieres gestionar el aprendizaje de tus estudiantes",
                icon = Icons.Rounded.School,
                onClick = onTeacherClick
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RoleSelectScreenPreview() {
    AlphakidsTheme {
        RoleSelectScreen(
            onTutorClick = {},
            onTeacherClick = {}
        )
    }
}
