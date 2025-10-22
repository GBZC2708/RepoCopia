package com.example.alphakids.ui.screens.tutor.games

import ScannerOverlay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alphakids.ui.components.NotificationCard
import com.example.alphakids.ui.components.TimerBar
import com.example.alphakids.ui.screens.tutor.games.components.CameraActionBar
import com.example.alphakids.ui.theme.AlphakidsTheme

@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    onFlashClick: () -> Unit,
    onShutterClick: () -> Unit,
    onFlipCameraClick: () -> Unit,
    onCloseNotificationClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        ScannerOverlay(
            modifier = Modifier.fillMaxSize()
        )

        TimerBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            progress = 0.2f,
            timeText = "0:00",
            isWarning = false
        )

        NotificationCard(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp, start = 24.dp, end = 24.dp),
            title = "Une la palabra",
            content = "Apunta a las letras",
            icon = Icons.Rounded.Checkroom,
            onCloseClick = onCloseNotificationClick
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }

        CameraActionBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onFlashClick = onFlashClick,
            onShutterClick = onShutterClick,
            onFlipCameraClick = onFlipCameraClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    AlphakidsTheme {
        CameraScreen(
            onBackClick = {},
            onFlashClick = {},
            onShutterClick = {},
            onFlipCameraClick = {},
            onCloseNotificationClick = {}
        )
    }
}
