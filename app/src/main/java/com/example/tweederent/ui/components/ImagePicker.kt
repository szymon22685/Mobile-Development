package com.example.tweederent.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagePicker(
    selectedImages: List<Uri>,
    onAddImages: () -> Unit,
    onRemoveImage: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Photos",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(selectedImages) { uri ->
                Box {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onRemoveImage(uri) },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {                        Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove image",
                        tint = MaterialTheme.colorScheme.error
                    )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onAddImages,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add photos")
                }
            }
        }
    }
}