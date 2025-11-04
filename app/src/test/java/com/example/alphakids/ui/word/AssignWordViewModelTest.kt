package com.example.alphakids.ui.word

import com.example.alphakids.data.firebase.models.Estudiante
import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.models.UserRole
import com.example.alphakids.domain.models.UserStatus
import com.example.alphakids.domain.models.Word
import com.example.alphakids.domain.usecases.CreateAssignmentUseCase
import com.example.alphakids.domain.usecases.GetCurrentUserUseCase
import com.example.alphakids.domain.usecases.GetFilteredWordsUseCase
import com.example.alphakids.domain.usecases.GetStudentsForDocenteUseCase
import com.example.alphakids.domain.usecases.IsWordAlreadyAssignedUseCase
import com.example.alphakids.fakes.FakeAssignmentRepository
import com.example.alphakids.fakes.FakeAuthRepository
import com.example.alphakids.fakes.FakeWordRepository
import com.example.alphakids.ui.word.assign.AssignWordViewModel
import com.example.alphakids.ui.word.assign.AssignmentUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AssignWordViewModelTest {

    @Test
    fun createAssignmentSucceeds() = runTest {
        val assignmentRepository = FakeAssignmentRepository().apply {
            setStudents(
                listOf(
                    Estudiante(
                        id = "student_1",
                        nombre = "Mateo",
                        apellido = "Diaz",
                        edad = 7,
                        grado = "Primaria",
                        seccion = "A",
                        idTutor = "tutor_1",
                        idDocente = "docente_1",
                        idInstitucion = "inst_1",
                        institucion = "Inst 1",
                        gradoAcademico = "Primaria",
                        seccionAcademica = "A",
                        docenteId = "docente_1"
                    )
                )
            )
        }
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
        val wordRepository = FakeWordRepository().apply {
            setWords(
                listOf(
                    Word(
                        id = "word_1",
                        texto = "Gato",
                        categoria = "Animales",
                        nivelDificultad = "Fácil",
                        imagenUrl = "",
                        audioUrl = "",
                        recompensaMonedas = 7,
                        fechaCreacionMillis = null,
                        creadoPor = "docente_1"
                    )
                )
            )
        }

        val viewModel = AssignWordViewModel(
            CreateAssignmentUseCase(assignmentRepository),
            GetStudentsForDocenteUseCase(assignmentRepository),
            GetFilteredWordsUseCase(wordRepository),
            GetCurrentUserUseCase(authRepository),
            IsWordAlreadyAssignedUseCase(assignmentRepository)
        )

        advanceUntilIdle()

        viewModel.selectStudent("student_1")
        val word = wordRepository.getFilteredWords(
            docenteId = "docente_1",
            categoria = null,
            dificultad = null
        ).first().first()

        viewModel.createAssignment(word)
        advanceUntilIdle()

        assertIs<AssignmentUiState.Success>(viewModel.uiState.value)
    }
}
