package com.fisherfence.maritime.di

import android.content.Context
import androidx.room.Room
import com.fisherfence.maritime.data.local.FisherDatabase
import com.fisherfence.maritime.data.repository.FisherRepositoryImpl
import com.fisherfence.maritime.domain.repository.FisherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFisherRepository(
        repositoryImpl: FisherRepositoryImpl
    ): FisherRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): FisherDatabase {
        return Room.databaseBuilder(
            context,
            FisherDatabase::class.java,
            "fisherfence.db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
}
