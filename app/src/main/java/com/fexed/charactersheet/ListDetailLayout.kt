package com.fexed.charactersheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun ListDetailLayout() {
    var selectedItem by remember { mutableStateOf<String?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {

        // LEFT: Navigation / List
        NavigationRail(
            modifier = Modifier.fillMaxHeight()
        ) {
            NavigationRailItem(
                selected = selectedItem == "home",
                onClick = { selectedItem = "home" },
                icon = { Icon(Icons.Default.Home, null) },
                label = { Text("Home") }
            )
            NavigationRailItem(
                selected = selectedItem == "settings",
                onClick = { selectedItem = "settings" },
                icon = { Icon(Icons.Default.Settings, null) },
                label = { Text("Settings") }
            )
        }

        // RIGHT: Detail
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            when (selectedItem) {
                "home" -> HomeScreen()
                "settings" -> SettingsScreen()
                else -> EmptyState()
            }
        }
    }
}
