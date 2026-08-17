package com.example.shoppinglist.ui.list

import com.example.shoppinglist.domain.model.Category
import com.example.shoppinglist.domain.model.ShoppingItem

/** Sort options offered in the list toolbar. */
enum class SortOption(val label: String) {
    NEWEST("Newest first"),
    NAME("Name (A–Z)"),
    CATEGORY("Category"),
    PURCHASED("Unpurchased first")
}

/**
 * Immutable UI state for the shopping list screen. Produced by the ViewModel
 * and consumed by the screen, following unidirectional data flow.
 *
 * @param items        The filtered + sorted items to display.
 * @param groupedItems Items grouped by category (used when [sortOption] is CATEGORY).
 * @param query        Current search text.
 * @param sortOption   Active sort.
 * @param totalCount   Total number of items (before filtering).
 * @param purchasedCount Number of purchased items (before filtering).
 * @param isLoading    True until the first DB emission arrives.
 */
data class ShoppingListUiState(
    val items: List<ShoppingItem> = emptyList(),
    val groupedItems: Map<Category, List<ShoppingItem>> = emptyMap(),
    val query: String = "",
    val sortOption: SortOption = SortOption.NEWEST,
    val totalCount: Int = 0,
    val purchasedCount: Int = 0,
    val isLoading: Boolean = true
) {
    /** True when there are no items at all (empty-state trigger). */
    val isEmpty: Boolean get() = !isLoading && totalCount == 0

    /** True when a search yields no matches but items do exist. */
    val isFilteredEmpty: Boolean get() = !isLoading && totalCount > 0 && items.isEmpty()

    /** Human-readable summary, e.g. "3 of 8 purchased". */
    val summary: String get() = "$purchasedCount of $totalCount purchased"
}
