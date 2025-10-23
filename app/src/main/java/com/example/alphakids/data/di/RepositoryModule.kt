package com.example.alphakids.data.di

import com.example.alphakids.data.firebase.repository.AuthRepositoryImpl
import com.example.alphakids.data.firebase.repository.StudentRepositoryImpl
import com.example.alphakids.domain.repository.AuthRepository
import com.example.alphakids.domain.repository.StudentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindStudentRepository(impl: StudentRepositoryImpl): StudentRepository
}
