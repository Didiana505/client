package com.example.onlinecoursesclient.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.MaterialTheme
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    var searchHistory by rememberSaveable { mutableStateOf(listOf<String>()) }
    var isSearchFocused by rememberSaveable { mutableStateOf(false) }
    var showDropdown by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Поле поиска
        OutlinedTextField(
            value = searchText,
            onValueChange = { text ->
                onSearchTextChange(text)
                showDropdown = text.isEmpty() && searchHistory.isNotEmpty() && isSearchFocused
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isSearchFocused = focusState.isFocused
                    if (focusState.isFocused && searchText.isEmpty() && searchHistory.isNotEmpty()) {
                        showDropdown = true
                    } else if (!focusState.isFocused) {
                        showDropdown = false
                    }
                },
            placeholder = { Text("Поиск курсов") },
            singleLine = true,
            trailingIcon = {
                Row {
                    if (searchText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                if (searchText.isNotBlank() && !searchHistory.contains(searchText)) {
                                    searchHistory = listOf(searchText) + searchHistory
                                }
                                onSearchTextChange("")
                                showDropdown = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Очистить"
                            )
                        }
                    }
                    if (searchText.isBlank()) {
                        TextButton(onClick = onFilterClick) {
                            Text("Фильтр")
                        }
                    }
                }
            }
        )


        if (showDropdown && searchHistory.isNotEmpty() && searchText.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column {
                    searchHistory.forEach { historyItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSearchTextChange(historyItem)
                                    showDropdown = false
                                    searchHistory = listOf(historyItem) + searchHistory.filter { it != historyItem }
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = historyItem,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    searchHistory = searchHistory.filter { it != historyItem }
                                    if (searchHistory.isEmpty()) {
                                        showDropdown = false
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Удалить из истории",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        if (historyItem != searchHistory.last()) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}