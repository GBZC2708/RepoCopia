package com.example.alphakids.data.di

import com.example.alphakids.data.demo.DemoAchievementRepository
import com.example.alphakids.data.demo.DemoAssignmentRepository
import com.example.alphakids.data.demo.DemoAuthRepository
import com.example.alphakids.data.demo.DemoModeConfig
import com.example.alphakids.data.demo.DemoStudentRepository
import com.example.alphakids.data.demo.DemoWordRepository
import com.example.alphakids.data.demo.DemoDiscoverRepository
import com.example.alphakids.data.firebase.repository.AssignmentRepositoryImpl
import com.example.alphakids.data.firebase.repository.AuthRepositoryImpl
import com.example.alphakids.data.firebase.repository.ImageStorageRepositoryImpl
import com.example.alphakids.data.firebase.repository.StudentRepositoryImpl
import com.example.alphakids.data.firebase.repository.WordRepositoryImpl
import com.example.alphakids.data.firebase.repository.DiscoverRepositoryImpl
import com.example.alphakids.domain.repository.AssignmentRepository
import com.example.alphakids.domain.repository.AuthRepository
import com.example.alphakids.domain.repository.ImageStorageRepository
import com.example.alphakids.domain.repository.StudentRepository
import com.example.alphakids.domain.repository.WordRepository
import com.example.alphakids.domain.repository.DiscoverRepository
import com.example.alphakids.domain.repository.AchievementRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage // Importación necesaria para el constructor de ImageStorageRepositoryImpl
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
        return if (DemoModeConfig.isEnabled) {
            DemoAuthRepository()
        } else {
            AuthRepositoryImpl(auth, db)
        }
    }

    @Provides
    @Singleton
    fun provideStudentRepository(db: FirebaseFirestore): StudentRepository {
        return if (DemoModeConfig.isEnabled) {
            DemoStudentRepository()
        } else {
            StudentRepositoryImpl(db)
        }
    }

    @Provides
    @Singleton
    fun provideWordRepository(db: FirebaseFirestore): WordRepository {
        return if (DemoModeConfig.isEnabled) {
            DemoWordRepository()
        } else {
            WordRepositoryImpl(db)
        }
    }

    @Provides
    @Singleton
    fun provideAssignmentRepository(db: FirebaseFirestore): AssignmentRepository {
        return if (DemoModeConfig.isEnabled) {
            DemoAssignmentRepository()
        } else {
            AssignmentRepositoryImpl(db)
        }
    }

    @Provides
    @Singleton
    fun provideImageStorageRepository(storage: FirebaseStorage): ImageStorageRepository {
        return ImageStorageRepositoryImpl(storage)
    }

    @Provides
    @Singleton
    fun provideDiscoverRepository(db: FirebaseFirestore): DiscoverRepository {
        return if (DemoModeConfig.isEnabled) {
            DemoDiscoverRepository()
        } else {
            DiscoverRepositoryImpl(db)
        }
    }

    @Provides
    @Singleton
    fun provideAchievementRepository(): AchievementRepository {
        return DemoAchievementRepository()
    }
}
