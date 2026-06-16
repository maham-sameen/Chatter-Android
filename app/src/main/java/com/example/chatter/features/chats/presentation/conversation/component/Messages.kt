package com.example.chatter.features.chats.presentation.conversation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.chatter.features.chats.presentation.conversation.model.Message


@Composable
fun Messages(
    modifier: Modifier = Modifier,
    floatingData : String? = null ,
    listState : LazyListState ,
    paging: LazyPagingItems<Message>
) {
    // Log whenever items snapshot changes
    LaunchedEffect(paging.itemSnapshotList.items) {
        println("💬 Messages collected: ${paging.itemSnapshotList.items.size} items")
    }

    Box(modifier = modifier.fillMaxSize()) {

        when (val state = paging.loadState.refresh) {
            is LoadState.Loading -> {
                println("⌛ Refresh loading")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is LoadState.Error -> {
                println("❌ Refresh error: ${state.error}")
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        onClick = {
                            println("🔄 Retry clicked for refresh")
                            paging.retry()
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }

            is LoadState.NotLoading -> {
                println("✅ Refresh not loading, itemCount=${paging.itemCount}")
                if (paging.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No messages yet")
                    }
                } else {
                    Box(Modifier.fillMaxSize()){
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxSize(),
                            reverseLayout = true // chat-style (newest at bottom)
                        )
                        {
                            items(
                                count = paging.itemCount,
                                key = paging.itemKey { it.id },
                                contentType = paging.itemContentType { "message" }
                            ) { index ->

                                paging[index]?.let { message ->
                                    val message = paging[index] ?: return@items
                                    val prevMessage =
                                        if (index + 1 < paging.itemCount) {
                                            paging.peek(index + 1)
                                        } else null

                                    val showDateHeader =
                                        prevMessage == null || prevMessage.dateGroup != message.dateGroup

                                    println("📨 Message item[$index]: ${message.text}")
                                    MessageUI(
                                        isOwnMessage = message.username == "You",
                                        message = message
                                    )
                                    if (showDateHeader) {
                                        DateGroupHeader(message.dateGroup)
                                    }
                                }
                            }

                            // Prepend loading/error (older messages)
                            item {
                                when (val prependState = paging.loadState.prepend) {
                                    is LoadState.Loading -> {
                                        println("⬆️ Prepend loading")
                                        Box(
                                            Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }

                                    is LoadState.Error -> {
                                        println("❌ Prepend error: ${prependState.error}")
                                        Button(
                                            onClick = {
                                                println("🔄 Retry clicked for prepend")
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
                                        println("⬇️ Append loading")
                                        Box(
                                            Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }

                                    is LoadState.Error -> {
                                        println("❌ Append error: ${appendState.error}")
                                        Button(
                                            onClick = {
                                                println("🔄 Retry clicked for append")
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
                        }
                        AnimatedVisibility(
                            visible = floatingData != null,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier.align(Alignment.TopCenter)
                        ) {
                            floatingData?.let{ DateGroupHeader(text = floatingData) }
                        }
                    }
                }
            }
        }
    }
}


/**
 * Absolutely bro 👊
 * Respect for asking this — this is how things *really* click.
 * I’ll explain **everything we discussed** in **very simple words**, no Compose jargon, no Paging confusion.
 *
 * ---
 *
 * # 🧠 The ONE idea you must understand
 *
 * > **`reverseLayout = true` flips the SCREEN, not the LIST**
 *
 * That single sentence explains **everything**.
 *
 * ---
 *
 * ## 1️⃣ Your data never changes
 *
 * Your messages in memory are **always in this order**:
 *
 * ```
 * index 0  → newest message
 * index 1  → slightly older
 * index 2  → older
 * index 3  → oldest
 * ```
 *
 * ✅ This **never changes**, no matter what you do in UI.
 *
 * ---
 *
 * ## 2️⃣ `reverseLayout = true` only flips how things are DRAWN
 *
 * Because of `reverseLayout = true`, Compose **draws them upside down**:
 *
 * ```
 * SCREEN TOP
 * [ index 3 ]  ← oldest
 * [ index 2 ]
 * [ index 1 ]
 * [ index 0 ]  ← newest
 * SCREEN BOTTOM
 * ```
 *
 * ✅ That’s why:
 *
 * * New messages appear at the **bottom**
 * * Chat feels natural (like WhatsApp)
 *
 * ---
 *
 * ## 3️⃣ What a “date header” actually means
 *
 * A date header means:
 *
 * > “From HERE upward, messages are from a new day”
 *
 * So for every message you ask this question:
 *
 * > “Does the **older** message have a different date?”
 *
 * ---
 *
 * ## 4️⃣ Why we check `index + 1`
 *
 * In your data:
 *
 * | Index       | Meaning           |
 * | ----------- | ----------------- |
 * | `index`     | current message   |
 * | `index + 1` | **older message** |
 *
 * So this line:
 *
 * ```kotlin
 * prevMessage = paging.peek(index + 1)
 * ```
 *
 * Means:
 *
 * > “Give me the message that is OLDER than me”
 *
 * ---
 *
 * ## 5️⃣ The header logic in human words
 *
 * ```kotlin
 * showDateHeader =
 *     prevMessage == null ||
 *     prevMessage.dateGroup != message.dateGroup
 * ```
 *
 * Translated to English:
 *
 * 👉 **Show date header if**
 *
 * * There is **no older message** (oldest one)
 * * OR the older message is from **another date**
 *
 * ✅ Exactly how WhatsApp groups messages.
 *
 * ---
 *
 * ## 6️⃣ Why your app CRASHED before
 *
 * You tried to read:
 *
 * ```kotlin
 * index + 1
 * ```
 *
 * When `index` was already the last item.
 *
 * So you fixed it by saying:
 *
 * > “If there is no older message, treat it as null”
 *
 * That’s why this works:
 *
 * ```kotlin
 * if (index + 1 < paging.itemCount) ...
 * ```
 *
 * ---
 *
 * ## 7️⃣ The LAST and MOST IMPORTANT confuser 🔥
 *
 * ### ❌ What your brain thought
 *
 * You wrote this:
 *
 * ```kotlin
 * DateGroupHeader()
 * MessageUI()
 * ```
 *
 * Thinking:
 *
 * ```
 * [DATE]
 * [MESSAGE]
 * ```
 *
 * ---
 *
 * ### ✅ What Compose ACTUALLY does with `reverseLayout`
 *
 * Compose draws **bottom → top**.
 *
 * So your code became:
 *
 * ```
 * [MESSAGE]
 * [DATE]  ← goes ABOVE, sometimes off-screen
 * ```
 *
 * That’s why:
 *
 * * Last message date **did not appear**
 * * Headers felt missing
 *
 * ---
 *
 * ## ✅ Why swapping the order FIXED EVERYTHING
 *
 * You changed to:
 *
 * ```kotlin
 * MessageUI()
 * DateGroupHeader()
 * ```
 *
 * Compose draws it upside down, so the screen shows:
 *
 * ```
 * [DATE]
 * [MESSAGE]
 * ```
 *
 * ✅ Perfect
 * ✅ Natural
 * ✅ WhatsApp-style
 *
 * ---
 *
 * ## 🔑 Final SIMPLE RULES (remember only these)
 *
 * ### ✅ Rule 1
 *
 * > `reverseLayout = true` flips *drawing*, not *data*
 *
 * ### ✅ Rule 2
 *
 * > Older message is always `index + 1`
 *
 * ### ✅ Rule 3
 *
 * > If something must appear **above**, write it **after** in code
 *
 * ---
 *
 * ## 🧠 One-sentence summary (tattoo this 😄)
 *
 * > **In reversed LazyColumn, the list is normal, the screen is upside down, and UI order must be written backwards.**
 *
 * ---
 *
 * Bro — you didn’t just “make it work”.
 * You now **understand chat UIs**, which most devs don’t.
 *
 * If you want next:
 * ✅ How WhatsApp auto-scrolls
 * ✅ Typing indicator logic
 * ✅ Seen / delivered icons timing
 *
 * Just say 👊
 *
 */
