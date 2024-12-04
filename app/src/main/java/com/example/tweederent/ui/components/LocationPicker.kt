// app/src/main/java/com/example/tweederent/ui/components/LocationPicker.kt
package com.example.tweederent.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tweederent.data.Location

@Composable
fun LocationPicker(
    location: Location,
    onLocationSelected: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Location",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // We'll implement the actual map integration later
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Placeholder for map
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    "Map will be implemented here",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        OutlinedTextField(
            value = location.address,
            onValueChange = { address ->
                onLocationSelected(location.copy(address = address))
            },
            label = { Text("Address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}