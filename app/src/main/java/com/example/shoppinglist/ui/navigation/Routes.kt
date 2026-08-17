package com.example.shoppinglist.ui.navigation

/**
 * Type-safe route definitions for Navigation Compose.
 *
 * The add/edit screen is reused for both creating and editing: passing an
 * itemId argument switches it into edit mode; the [NEW_ITEM_ID] sentinel means
 * "create a new item".
 */
object Routes {
    const val LIST = "list"

    const val ITEM_ID_ARG = "itemId"
    const val NEW_ITEM_ID = -1L

    /** Base route pattern registered in the NavHost. */
    const val ADD_EDIT = "add_edit?$ITEM_ID_ARG={$ITEM_ID_ARG}"

    /** Build a concrete route for adding a new item. */
    fun addItem(): String = "add_edit?$ITEM_ID_ARG=$NEW_ITEM_ID"

    /** Build a concrete route for editing an existing item. */
    fun editItem(itemId: Long): String = "add_edit?$ITEM_ID_ARG=$itemId"
}
