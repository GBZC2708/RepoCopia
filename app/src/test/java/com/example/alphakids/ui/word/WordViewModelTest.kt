package com.example.alphakids.ui.word

import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.domain.models.UserStatus
import com.example.alphakids.domain.repository.WordSortOrder
import com.example.alphakids.domain.usecases.CreateWordUseCase
import com.example.alphakids.domain.usecases.DeleteWordUseCase
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.GetFilteredWordsUseCase
import com.example.alphakids.domain.usecases.UpdateWordUseCase
import com.example.alphakids.fakes.FakeAuthRepository
import com.example.alphakids.fakes.FakeImageStorageRepository
import com.example.alphakids.fakes.FakeWordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class WordViewModelTest {

    @Test
    fun createWordAddsEntry() = runTest {
        val authRepository = FakeAuthRepository(
            User(
                uid = "docente_1",
                nombre = "Laura",
                apellido = "Ramirez",
                email = "docente@test.com",
                rol = UserRole.DOCENTE,
                estado = UserStatus.ACTIVO,
                monedas = 0
            )
        )
        val wordRepository = FakeWordRepository()
        val imageRepository = FakeImageStorageRepository()

        val viewModel = WordViewModel(
            CreateWordUseCase(wordRepository, imageRepository),
            UpdateWordUseCase(wordRepository),
            DeleteWordUseCase(wordRepository),
            GetFilteredWordsUseCase(wordRepository),
            GetCurrentUserUseCase(authRepository)
        )

        viewModel.createWord(
            texto = "Gato",
            categoria = "Animales",
            nivelDificultad = "Fácil",
            audioUrl = "audio",
            recompensaMonedas = 10
        )

        advanceUntilIdle()

        assertIs<WordUiState.Success>(viewModel.uiState.value)
        val storedWords = wordRepository.getAllWords(WordSortOrder.TEXT_ASC).first()
        assertEquals(1, storedWords.size)
        assertEquals(10, storedWords.first().recompensaMonedas)
    }
}
