package com.example.shoppinglist.data.repository

import com.example.shoppinglist.data.local.ShoppingDao
import com.example.shoppinglist.data.mapper.toDomain
import com.example.shoppinglist.data.mapper.toEntity
import com.example.shoppinglist.domain.model.ShoppingItem
import com.example.shoppinglist.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room-backed implementation of [ShoppingRepository].
 *
 * Translates between domain models and Room entities and delegates all storage
 * to the [ShoppingDao]. Constructor-injected by Hilt as a singleton.
 */
@Singleton
class ShoppingRepositoryImpl @Inject constructor(
    private val dao: ShoppingDao
) : ShoppingRepository {

    override fun observeItems(): Flow<List<ShoppingItem>> =
        dao.observeItems().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getItem(id: Long): ShoppingItem? =
        dao.getById(id)?.toDomain()

    override suspend fun addItem(item: ShoppingItem): Long =
        dao.insert(item.toEntity())

    override suspend fun updateItem(item: ShoppingItem) =
        dao.update(item.toEntity())

    override suspend fun setPurchased(id: Long, isPurchased: Boolean) =
        dao.setPurchased(id, isPurchased)

    override suspend fun deleteItem(item: ShoppingItem) =
        dao.delete(item.toEntity())
}
