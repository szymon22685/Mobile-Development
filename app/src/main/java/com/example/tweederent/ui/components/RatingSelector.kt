package com.example.tweederent.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RatingSelector(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Int = 24
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            IconButton(
                onClick = { if (enabled) onRatingChange(index + 1) },
                enabled = enabled
            ) {
                Icon(
                    imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "Rate ${index + 1} stars",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(size.dp)
                )
            }
        }
    }
}

@Composable
fun RatingDisplay(
    rating: Int,
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(size.dp)
            )
        }
    }
}