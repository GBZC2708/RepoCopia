package com.example.alphakids.data.firebase.repository

import android.util.Log
import com.example.alphakids.data.firebase.models.Docente
import com.example.alphakids.data.firebase.models.Tutor
import com.example.alphakids.data.firebase.models.Usuario
import com.example.alphakids.data.mappers.UsuarioMapper
import com.example.alphakids.domain.models.User
import com.example.alphakids.domain.repository.AuthRepository
import com.example.alphakids.domain.repository.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepository {

    private val usuariosCol = db.collection("usuarios")
    private val docentesCol = db.collection("docentes")
    private val tutoresCol = db.collection("tutores")

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        var firestoreRegistration: ListenerRegistration? = null

        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            firestoreRegistration?.remove()
            firestoreRegistration = null

            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                val userDocRef = usuariosCol.document(firebaseUser.uid)
                Log.d("AuthRepo", "Setting up Firestore listener for user: ${firebaseUser.uid}")

                firestoreRegistration = userDocRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("AuthRepo", "Firestore listener error for user ${firebaseUser.uid}", error)
                        trySend(null)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val usuarioDto = snapshot.toObject(Usuario::class.java)
                        if (usuarioDto != null) {
                            Log.d("AuthRepo", "User data received for ${firebaseUser.uid}: ${usuarioDto.nombre}")
                            trySend(UsuarioMapper.toDomain(usuarioDto))
                        } else {
                            Log.w("AuthRepo", "Failed to parse user document for ${firebaseUser.uid}")
                            trySend(null)
                        }
                    } else {
                        Log.w("AuthRepo", "User document for ${firebaseUser.uid} does not exist or access denied.")
                        trySend(null)
                    }
                }
            }
        }

        auth.addAuthStateListener(authListener)

        awaitClose {
            Log.d("AuthRepo", "Removing listeners on awaitClose")
            auth.removeAuthStateListener(authListener)
            firestoreRegistration?.remove()
        }
    }


    override fun register(
        nombre: String,
        apellido: String,
        email: String,
        clave: String,
        telefono: String,
        rol: String
    ): AuthResult = flow {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, clave).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Error al crear usuario, FirebaseUser es nulo.")

            val uid = firebaseUser.uid

            val usuario = Usuario(
                uid = uid,
                nombre = nombre,
                apellido = apellido,
                email = email,
                telefono = telefono,
                rol = rol,
                estado = "activo",
            )

            val batch = db.batch()
            batch.set(usuariosCol.document(uid), usuario)

            if (rol == "docente") {
                val docente = Docente(uid = uid)
                batch.set(docentesCol.document(uid), docente)
            } else if (rol == "tutor") {
                val tutor = Tutor(uid = uid)
                batch.set(tutoresCol.document(uid), tutor)
            }

            batch.commit().await()

            emit(Result.success(UsuarioMapper.toDomain(usuario)))

        } catch (e: FirebaseAuthWeakPasswordException) {
            emit(Result.failure(Exception("La contraseña es demasiado débil.")))
        } catch (e: FirebaseAuthUserCollisionException) {
            emit(Result.failure(Exception("El correo electrónico ya está en uso.")))
        } catch (e: Exception) {
            Log.e("AuthRepo", "Registration failed", e)
            emit(Result.failure(Exception("Error durante el registro: ${e.message}")))
        }
    }

    override fun login(email: String, clave: String): AuthResult = flow {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, clave).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Error al iniciar sesión.")

            val userDoc = usuariosCol.document(firebaseUser.uid).get().await()
            val usuarioDto = userDoc.toObject(Usuario::class.java)
                ?: throw IllegalStateException("No se encontró el perfil de usuario en Firestore.")

            emit(Result.success(UsuarioMapper.toDomain(usuarioDto)))

        } catch (e: Exception) {
            Log.e("AuthRepo", "Login failed", e)
            emit(Result.failure(Exception("Correo o contraseña incorrectos.")))
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
