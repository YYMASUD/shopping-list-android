package com.example.shoppinglist.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for shopping items.
 *
 * Reads return a [Flow] so the UI observes the database reactively: any write
 * automatically re-emits the list and the screen recomposes. Writes are
 * suspend functions run off the main thread from the repository.
 */
@Dao
interface ShoppingDao {

    /** Reactive list of all items, newest-created first. */
    @Query("SELECT * FROM shopping_items ORDER BY createdAt DESC")
    fun observeItems(): Flow<List<ShoppingItemEntity>>

    /** Single item lookup used when opening the edit screen. */
    @Query("SELECT * FROM shopping_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ShoppingItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItemEntity): Long

    @Update
    suspend fun update(item: ShoppingItemEntity)

    /** Targeted purchased-flag update to avoid a full-row round trip. */
    @Query("UPDATE shopping_items SET isPurchased = :isPurchased WHERE id = :id")
    suspend fun setPurchased(id: Long, isPurchased: Boolean)

    @Delete
    suspend fun delete(item: ShoppingItemEntity)
}
