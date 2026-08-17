package com.example.shoppinglist.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Compact progress header, e.g. "3 of 8 purchased" plus a progress bar.
 * Purely presentational; counts are passed in from the UI state.
 */
@Composable
fun SummaryHeader(
    purchased: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (total == 0) 0f else purchased.toFloat() / total.toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics { contentDescription = "$purchased of $total items purchased" }
    ) {
        Text(
            text = "$purchased of $total purchased",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}
