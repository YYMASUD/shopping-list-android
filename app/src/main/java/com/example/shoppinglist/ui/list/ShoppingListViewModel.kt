package com.example.shoppinglist.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.domain.model.ShoppingItem
import com.example.shoppinglist.domain.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the shopping list screen.
 *
 * Combines three reactive inputs — the persisted items (from Room), the search
 * query, and the sort option — into a single [ShoppingListUiState] exposed as a
 * [StateFlow]. All user actions funnel through the methods below, which delegate
 * to the repository. This is the single source of truth for the list UI.
 */
@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val repository: ShoppingRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val sortOption = MutableStateFlow(SortOption.NEWEST)

    /**
     * Ids of items the user has swiped away but not yet permanently deleted.
     *
     * While an id sits here the item is hidden from the list but still lives in
     * the database, so an Undo simply removes the id (instant, reliable restore).
     * The actual DB delete is committed only when the Snackbar is dismissed
     * without an Undo (see [onDeleteCommitted]).
     */
    private val pendingDeletes = MutableStateFlow<Set<Long>>(emptySet())

    val uiState: StateFlow<ShoppingListUiState> =
        combine(
            repository.observeItems(),
            query,
            sortOption,
            pendingDeletes
        ) { items, currentQuery, sort, pending ->
            val visible = items.filterNot { it.id in pending }
            buildState(visible, currentQuery, sort)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ShoppingListUiState()
        )

    /** Pure transformation of raw items -> displayable, filtered, sorted state. */
    private fun buildState(
        items: List<ShoppingItem>,
        currentQuery: String,
        sort: SortOption
    ): ShoppingListUiState {
        val trimmed = currentQuery.trim()
        val filtered = if (trimmed.isEmpty()) {
            items
        } else {
            items.filter { it.name.contains(trimmed, ignoreCase = true) }
        }

        val sorted = when (sort) {
            SortOption.NEWEST -> filtered.sortedByDescending { it.createdAt }
            SortOption.NAME -> filtered.sortedBy { it.name.lowercase() }
            SortOption.CATEGORY -> filtered.sortedWith(
                compareBy({ it.category.displayName }, { it.name.lowercase() })
            )
            SortOption.PURCHASED -> filtered.sortedWith(
                compareBy({ it.isPurchased }, { it.name.lowercase() })
            )
        }

        return ShoppingListUiState(
            items = sorted,
            groupedItems = sorted.groupBy { it.category },
            query = currentQuery,
            sortOption = sort,
            totalCount = items.size,
            purchasedCount = items.count { it.isPurchased },
            isLoading = false
        )
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onSortChange(option: SortOption) {
        sortOption.value = option
    }

    /** Toggle purchased state; persisted immediately so it survives restarts. */
    fun onTogglePurchased(item: ShoppingItem) {
        viewModelScope.launch {
            repository.setPurchased(item.id, !item.isPurchased)
        }
    }

    /**
     * Called when the user swipes an item away. The item is hidden from the list
     * immediately (optimistic) but kept in the database so it can be restored
     * instantly. Call [onDeleteCommitted] to finalize or [onUndoDelete] to cancel.
     */
    fun onDelete(item: ShoppingItem) {
        pendingDeletes.value = pendingDeletes.value + item.id
    }

    /** Cancel a pending delete — the item reappears in the list. */
    fun onUndoDelete(item: ShoppingItem) {
        pendingDeletes.value = pendingDeletes.value - item.id
    }

    /**
     * Finalize a pending delete by removing the row from the database. Invoked
     * when the undo Snackbar is dismissed without the user tapping Undo. No-op if
     * the delete was already undone.
     */
    fun onDeleteCommitted(item: ShoppingItem) {
        if (item.id !in pendingDeletes.value) return
        viewModelScope.launch {
            repository.deleteItem(item)
            pendingDeletes.value = pendingDeletes.value - item.id
        }
    }
}
