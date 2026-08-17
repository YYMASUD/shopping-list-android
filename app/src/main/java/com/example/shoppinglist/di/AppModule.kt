package com.example.shoppinglist.di

import android.content.Context
import androidx.room.Room
import com.example.shoppinglist.data.local.ShoppingDao
import com.example.shoppinglist.data.local.ShoppingDatabase
import com.example.shoppinglist.data.repository.ShoppingRepositoryImpl
import com.example.shoppinglist.domain.repository.ShoppingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module wiring the data layer.
 *
 * [DataModule] provides the concrete Room objects (database + DAO), while
 * [RepositoryModule] binds the repository interface to its implementation so
 * consumers depend only on the domain contract.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ShoppingDatabase = Room.databaseBuilder(
        context,
        ShoppingDatabase::class.java,
        ShoppingDatabase.DATABASE_NAME
    ).build()

    @Provides
    fun provideShoppingDao(database: ShoppingDatabase): ShoppingDao =
        database.shoppingDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindShoppingRepository(
        impl: ShoppingRepositoryImpl
    ): ShoppingRepository
}
