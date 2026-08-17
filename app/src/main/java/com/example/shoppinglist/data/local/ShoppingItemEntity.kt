package com.example.shoppinglist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room table row for a shopping item.
 *
 * Persistence-specific concerns (the table name, primary key, and how the
 * category is stored) live here so the domain model stays clean. The category
 * is stored as its enum [name] String for forward-compatible, human-readable rows.
 */
@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val quantity: Int,
    val category: String,
    val isPurchased: Boolean,
    val createdAt: Long
)
