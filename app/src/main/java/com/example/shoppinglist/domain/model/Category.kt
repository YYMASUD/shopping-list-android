package com.example.shoppinglist.domain.model

/**
 * Categories a shopping item can belong to.
 *
 * Kept as an enum so the set is fixed and type-safe across the UI, ViewModel,
 * and persistence layers. [displayName] is the user-facing label.
 */
enum class Category(val displayName: String) {
    PRODUCE("Produce"),
    DAIRY("Dairy"),
    BAKERY("Bakery"),
    MEAT("Meat & Fish"),
    FROZEN("Frozen"),
    PANTRY("Pantry"),
    BEVERAGES("Beverages"),
    HOUSEHOLD("Household"),
    OTHER("Other");

    companion object {
        /** Safe lookup used when mapping persisted values back to the enum. */
        fun fromName(value: String?): Category =
            entries.firstOrNull { it.name == value } ?: OTHER
    }
}
