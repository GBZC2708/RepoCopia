package com.example.alphakids.ui.screens.tutor.games

import com.example.alphakids.fakes.FakeAssignmentRepository
import com.example.alphakids.domain.usecases.CompleteAssignmentUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class CameraOCRViewModelTest {

    @Test
    fun processDetectedTextMarksSuccess() = runBlocking {
        val viewModel = CameraOCRViewModel(
            CompleteAssignmentUseCase(FakeAssignmentRepository())
        )

        viewModel.setTargetWord("Gato")
        viewModel.processDetectedText("  ¡gáto!  ")

        assertTrue(viewModel.uiState.value.isWordDetected)
    }
}
