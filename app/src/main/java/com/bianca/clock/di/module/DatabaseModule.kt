package com.bianca.clock.di.module

import android.content.Context
import androidx.room.Room
import com.bianca.clock.infrastructure.room.AppDatabase
import com.bianca.clock.infrastructure.room.TaskDao
import com.bianca.clock.viewModel.repo.ITaskRepository
import com.bianca.clock.viewModel.repo.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "focusflow_db"
        ).build()
    }

    @Provides
    fun provideTaskDao(
        db: AppDatabase,
    ): TaskDao = db.taskDao()

    @Provides
    fun provideTaskRepository(
        taskDao: TaskDao,
    ): ITaskRepository = TaskRepository(taskDao)
}