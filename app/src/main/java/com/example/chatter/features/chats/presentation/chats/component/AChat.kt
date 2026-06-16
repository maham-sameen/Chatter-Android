package com.example.chatter.features.chats.presentation.chats.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.chatter.core.presentation.Profile
import com.example.chatter.features.chats.presentation.chats.ChatRoom
import com.example.chatter.ui.ColorScheme
import com.example.chatter.ui.theme.blue400


@Preview
@Composable
fun AChatPreview() {
    val chatRoom = ChatRoom(
        profilePath = null,
        name = "Mahamojopuopeupu3irp3irppw0-wirp[krnkljdlqwjdlwdowidopwipowdp",
        timeStamp = "7.80 pm",
        lastMessage = "",
        color = ColorScheme(
            icon = blue400,
            background = blue400
        ),
        roomId = "",
        unreadMessages = 2
    )
    AChat(chatRoom , {})
}

@Composable
fun AChat(
    chatRoom: ChatRoom ,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Profile(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(50.dp),
            profile = chatRoom.profilePath?.let {
                {
                    println("image $it")
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null ,
                        contentScale = ContentScale.Crop ,
                        modifier = Modifier.fillMaxSize(), // ✅ this makes it fill the circle
                    )
                }
            },
            color = chatRoom.color.background,
            firstCapitalLetterTxt = {
                Text(
                    text =  chatRoom.name[0].uppercase() ,
                    color = chatRoom.color.icon
                )
            }
        )
        Column(
            modifier = Modifier.weight(1f)
        // make column take available space
        ) {
            Row(
                verticalAlignment = if(chatRoom.lastMessage.isNotEmpty()) {
                    Alignment.Top
                }else{
                    Alignment.CenterVertically
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chatRoom.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f) // allow text to use max width
                )

                // Timestamp
                Text(
                    text = chatRoom.timeStamp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        top = 2.dp,
                        start = 2.dp,
                        end = 8.dp)
                )

                // Unread badge
                if (chatRoom.unreadMessages > 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .background(
                                color = blue400,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = chatRoom.unreadMessages.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            if(chatRoom.lastMessage.isNotEmpty()){
                Text(
                    text = chatRoom.lastMessage,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}