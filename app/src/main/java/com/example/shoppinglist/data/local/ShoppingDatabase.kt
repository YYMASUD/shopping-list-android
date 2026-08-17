package com.example.shoppinglist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The app's Room database. Holds the single [ShoppingItemEntity] table and
 * exposes the [ShoppingDao]. Provided as a singleton via Hilt (see AppModule).
 */
@Database(
    entities = [ShoppingItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao

    companion object {
        const val DATABASE_NAME = "shopping_list.db"
    }
}
