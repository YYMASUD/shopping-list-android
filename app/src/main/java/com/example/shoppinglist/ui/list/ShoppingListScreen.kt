package com.example.shoppinglist.ui.list

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shoppinglist.domain.model.ShoppingItem
import com.example.shoppinglist.ui.components.EmptyState
import com.example.shoppinglist.ui.components.NoResultsState
import com.example.shoppinglist.ui.components.SearchBar
import com.example.shoppinglist.ui.components.ShoppingListItemCard
import com.example.shoppinglist.ui.components.SortMenu
import com.example.shoppinglist.ui.components.SummaryHeader
import kotlinx.coroutines.launch

/**
 * The main shopping list screen (list route).
 *
 * Collects [ShoppingListUiState] from the ViewModel and renders the toolbar,
 * search, summary, and item list. Navigation callbacks are hoisted so this
 * composable stays free of NavController dependencies and easy to preview/test.
 * Swipe-to-delete shows a Snackbar with an Undo action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    onAddItem: () -> Unit,
    onEditItem: (Long) -> Unit,
    viewModel: ShoppingListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Shopping List") },
                actions = {
                    SortMenu(
                        current = state.sortOption,
                        onSortChange = viewModel::onSortChange
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItem) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> LoadingContent()

                state.isEmpty -> EmptyState()

                else -> {
                    // Search + summary are always shown once items exist.
                    SearchBar(
                        query = state.query,
                        onQueryChange = viewModel::onQueryChange
                    )
                    SummaryHeader(
                        purchased = state.purchasedCount,
                        total = state.totalCount
                    )

                    if (state.isFilteredEmpty) {
                        NoResultsState(query = state.query)
                    } else {
                        ItemList(
                            items = state.items,
                            onToggle = viewModel::onTogglePurchased,
                            onEdit = { onEditItem(it.id) },
                            onDelete = { item ->
                                viewModel.onDelete(item)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Deleted \"${item.name}\"",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.onUndoDelete(item)
                                    } else {
                                        // Dismissed/timed out without Undo -> finalize deletion.
                                        viewModel.onDeleteCommitted(item)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ItemList(
    items: List<ShoppingItem>,
    onToggle: (ShoppingItem) -> Unit,
    onEdit: (ShoppingItem) -> Unit,
    onDelete: (ShoppingItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 8.dp,
            bottom = 96.dp // leave room for the FAB
        )
    ) {
        items(items, key = { it.id }) { item ->
            ShoppingListItemCard(
                item = item,
                onToggle = { onToggle(item) },
                onEdit = { onEdit(item) },
                onDelete = { onDelete(item) },
                modifier = Modifier.animateItem()
            )
        }
    }
}
