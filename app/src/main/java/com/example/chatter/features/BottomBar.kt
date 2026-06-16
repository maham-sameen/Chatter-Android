package com.example.chatter.features

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chatter.features.utils.BottomBarScreen
import com.example.chatter.ui.theme.blue400


@Composable
fun BottomBar(
    screens : List<BottomBarScreen>,
    selected : (BottomBarScreen) -> Boolean,
    onClick : (BottomBarScreen) -> Unit
) {
    NavigationBar(
        content = {
            screens.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selected(item),
                    onClick = {
                        onClick(item)
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                when {
                                    item.badgeCount != null -> Badge { Text(text = item.badgeCount.toString()) }
                                    item.hasNews -> Badge()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.title,
                                tint = when {
                                    selected(item) -> blue400
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                },
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title, color = when {
                                selected(item) -> blue400
                                else -> MaterialTheme.colorScheme.onSecondaryContainer
                            }
                        )
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    )
}
