package com.example.shoppinglist.ui.additem

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.domain.model.Category
import com.example.shoppinglist.domain.model.ShoppingItem
import com.example.shoppinglist.domain.repository.ShoppingRepository
import com.example.shoppinglist.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Form state for the add/edit screen. Holds the raw text fields plus inline
 * validation errors so the UI can show helper text without extra state.
 */
data class AddEditUiState(
    val name: String = "",
    val quantity: String = "1",
    val category: Category = Category.OTHER,
    val isEditing: Boolean = false,
    val nameError: String? = null,
    val quantityError: String? = null,
    val isSaved: Boolean = false
)

/**
 * ViewModel backing the add/edit form.
 *
 * On construction it reads the optional itemId nav argument from
 * [SavedStateHandle]; a real id loads the existing item into the form (edit
 * mode), otherwise the form starts blank (add mode). [save] validates input,
 * persists via the repository, and flips [AddEditUiState.isSaved] so the screen
 * can navigate back.
 */
@HiltViewModel
class AddEditItemViewModel @Inject constructor(
    private val repository: ShoppingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: Long =
        savedStateHandle.get<Long>(Routes.ITEM_ID_ARG) ?: Routes.NEW_ITEM_ID

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        if (itemId != Routes.NEW_ITEM_ID) {
            loadItem(itemId)
        }
    }

    private fun loadItem(id: Long) {
        viewModelScope.launch {
            repository.getItem(id)?.let { item ->
                _uiState.update {
                    it.copy(
                        name = item.name,
                        quantity = item.quantity.toString(),
                        category = item.category,
                        isEditing = true
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onQuantityChange(value: String) {
        // Only accept digits to prevent invalid input up front.
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _uiState.update { it.copy(quantity = value, quantityError = null) }
        }
    }

    fun onCategoryChange(category: Category) {
        _uiState.update { it.copy(category = category) }
    }

    /** Validate and persist. Returns early with field errors on invalid input. */
    fun save() {
        val current = _uiState.value
        val trimmedName = current.name.trim()
        val quantityValue = current.quantity.toIntOrNull()

        val nameError = if (trimmedName.isBlank()) "Name can't be empty" else null
        val quantityError = when {
            quantityValue == null -> "Enter a valid number"
            quantityValue < 1 -> "Quantity must be at least 1"
            else -> null
        }

        if (nameError != null || quantityError != null) {
            _uiState.update { it.copy(nameError = nameError, quantityError = quantityError) }
            return
        }

        viewModelScope.launch {
            if (current.isEditing && itemId != Routes.NEW_ITEM_ID) {
                // Preserve the existing item's purchased state and creation time.
                val existing = repository.getItem(itemId)
                val updated = (existing ?: ShoppingItem(name = trimmedName, quantity = quantityValue!!)).copy(
                    name = trimmedName,
                    quantity = quantityValue!!,
                    category = current.category
                )
                repository.updateItem(updated)
            } else {
                repository.addItem(
                    ShoppingItem(
                        name = trimmedName,
                        quantity = quantityValue!!,
                        category = current.category
                    )
                )
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
