package com.example.shoppinglist.domain.model

/**
 * Immutable domain representation of a shopping list item.
 *
 * This is the model the UI and ViewModel work with. It intentionally contains
 * no persistence annotations (that lives on the Room entity) and no transient
 * UI flags such as "isEditing" (that state is hoisted into Compose). Keeping
 * the domain model clean makes the business rules easy to reason about and test.
 *
 * @param id          Stable identifier. 0 means "not yet persisted".
 * @param name        Item name (validated non-blank before creation).
 * @param quantity    Desired quantity (validated to be >= 1 before creation).
 * @param category    Grouping bucket for the item.
 * @param isPurchased Whether the item has been bought/checked off.
 * @param createdAt   Creation timestamp (epoch millis) used as a stable sort tie-breaker.
 */
data class ShoppingItem(
    val id: Long = 0L,
    val name: String,
    val quantity: Int,
    val category: Category = Category.OTHER,
    val isPurchased: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
