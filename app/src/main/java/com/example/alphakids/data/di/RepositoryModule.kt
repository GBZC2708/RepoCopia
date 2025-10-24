package com.example.alphakids.data.di

import com.example.alphakids.data.firebase.repository.AssignmentRepositoryImpl
import com.example.alphakids.data.firebase.repository.AuthRepositoryImpl
import com.example.alphakids.data.firebase.repository.StudentRepositoryImpl
import com.example.alphakids.data.firebase.repository.WordRepositoryImpl
import com.example.alphakids.domain.repository.AssignmentRepository
import com.example.alphakids.domain.repository.AuthRepository
import com.example.alphakids.domain.repository.StudentRepository
import com.example.alphakids.domain.repository.WordRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, db: FirebaseFirestore): AuthRepository {
        return AuthRepositoryImpl(auth, db)
    }

    @Provides
    @Singleton
    fun provideStudentRepository(db: FirebaseFirestore): StudentRepository {
        return StudentRepositoryImpl(db)
    }

    @Provides
    @Singleton
    fun provideWordRepository(db: FirebaseFirestore): WordRepository {
        return WordRepositoryImpl(db)
    }

    @Provides
    @Singleton
    fun provideAssignmentRepository(db: FirebaseFirestore): AssignmentRepository {
        return AssignmentRepositoryImpl(db)
    }
}
