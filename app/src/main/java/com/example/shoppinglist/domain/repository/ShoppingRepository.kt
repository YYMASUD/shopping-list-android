package com.example.shoppinglist.domain.repository

import com.example.shoppinglist.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

/**
 * Contract for shopping item persistence, owned by the domain layer.
 *
 * The UI/ViewModel depend on this interface rather than a concrete Room
 * implementation, which keeps the domain independent of the data layer and
 * makes the ViewModel easy to unit test with a fake repository.
 */
interface ShoppingRepository {

    /** Reactive stream of all items. Emits a new list whenever the data changes. */
    fun observeItems(): Flow<List<ShoppingItem>>

    /** Fetch a single item by id, or null if it no longer exists. */
    suspend fun getItem(id: Long): ShoppingItem?

    /** Insert a new item and return its generated id. */
    suspend fun addItem(item: ShoppingItem): Long

    /** Update an existing item in place. */
    suspend fun updateItem(item: ShoppingItem)

    /** Toggle the purchased flag for the given item id. */
    suspend fun setPurchased(id: Long, isPurchased: Boolean)

    /** Delete an item. */
    suspend fun deleteItem(item: ShoppingItem)
}
