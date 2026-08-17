package com.example.shoppinglist.data.mapper

import com.example.shoppinglist.data.local.ShoppingItemEntity
import com.example.shoppinglist.domain.model.Category
import com.example.shoppinglist.domain.model.ShoppingItem

/**
 * Conversion helpers between the Room [ShoppingItemEntity] and the domain
 * [ShoppingItem]. Isolating mapping here keeps both models free of each other's
 * concerns and gives a single place to evolve the storage format.
 */

/** Entity -> domain model. */
fun ShoppingItemEntity.toDomain(): ShoppingItem = ShoppingItem(
    id = id,
    name = name,
    quantity = quantity,
    category = Category.fromName(category),
    isPurchased = isPurchased,
    createdAt = createdAt
)

/** Domain model -> entity. */
fun ShoppingItem.toEntity(): ShoppingItemEntity = ShoppingItemEntity(
    id = id,
    name = name,
    quantity = quantity,
    category = category.name,
    isPurchased = isPurchased,
    createdAt = createdAt
)
