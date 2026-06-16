package com.example.chatter.features.calls.presentation.call_history.compoenent

import androidx.compose.foundation.layout.Arrangement
import com.example.chatter.features.calls.presentation.call_history.model.Call
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey


@Composable
fun Calls(
    listState : LazyListState ,
    modifier: Modifier = Modifier,
    paging: LazyPagingItems<Call>
) {
    // Log whenever items snapshot changes
    LaunchedEffect(paging.itemSnapshotList.items) {
        println("💬 [Calls] collected: ${paging.itemSnapshotList.items.size} items")
    }

    Box(modifier = modifier.fillMaxSize()) {

        when (val state = paging.loadState.refresh) {
            is LoadState.Loading -> {
                println(" [Calls]⌛ Refresh loading")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> {
                println("❌[Calls] Refresh error: ${state.error}")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        onClick = {
                            println("🔄[Calls] Retry clicked for refresh")
                            paging.retry()
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }

            is LoadState.NotLoading -> {
                println("✅[Calls] Refresh not loading, itemCount=${paging.itemCount}")
                if (paging.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No calls yet")
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(5.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            count = paging.itemCount,
                            key = paging.itemKey { it.callId },
                            contentType = paging.itemContentType { "call" }
                        ) { index ->
                            paging[index]?.let { call ->
                                println("📨 [Calls] item [$index]: call  ${call.toString()}")
                                ACall(
                                    call = call
                                )
                            }
                        }

                        // Prepend loading/error (older messages)
                        item {
                            when (val prependState = paging.loadState.prepend) {
                                is LoadState.Loading -> {
                                    println("⬆️ [Calls] Prepend loading")
                                    Box(
                                        Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                is LoadState.Error -> {
                                    println("❌[Calls]  Prepend error: ${prependState.error}")
                                    Button(
                                        onClick = {
                                            println("🔄 [Calls] Retry clicked for prepend")
                                            paging.retry()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Retry")
                                    }
                                }
                                else -> {}
                            }
                        }

                        // Append loading/error (newer messages)
                        item {
                            when (val appendState = paging.loadState.append) {
                                is LoadState.Loading -> {
                                    println("⬇️ [Calls] Append loading")
                                    Box(
                                        Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                is LoadState.Error -> {
                                    println("❌ [Calls] Append error: ${appendState.error}")
                                    Button(
                                        onClick = {
                                            println("🔄 [Calls] Retry clicked for append")
                                            paging.retry()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Retry")
                                    }
                                }
                                else -> {

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
