package com.example.chatter.features.chats.presentation.conversation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.chatter.core.presentation.Profile
import com.example.chatter.core.presentation.topAppBarColors
import com.example.chatter.ui.ColorScheme
import com.example.chatter.ui.theme.colorSchemes
import com.example.chatter.R


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ConversationTopBarPreview() {
    ConversationTopBar(
        name = "Maham",
        image = null,
        presenceText = "Online",
        onVoiceCall = {},
        onVideoCall = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationTopBar(
    name : String ,
    image : String? ,
    presenceText : String ,
    onVoiceCall : () -> Unit ,
    onVideoCall : () -> Unit
) {
    val colorSchema = remember { colorSchemes.random() }
    val context = LocalContext.current
    Column {
        TopAppBar(
            title = {
                ConversationTitle(
                    colorScheme = colorSchema,
                    file = image,
                    name = name,
                    presenceText = presenceText
                )
            },
            modifier = Modifier,
            navigationIcon = {
                Icon(
                    painter = painterResource(R.drawable.outline_keyboard_arrow_left_24) ,
                    contentDescription = null ,
                    modifier = Modifier.size(28.dp)
                )
            },
            actions = {
                ConversationActions(
                    onVoiceCall = onVoiceCall,
                    onVideoCall = onVideoCall
                )
            },
            colors = topAppBarColors(),
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun ConversationTitle(
    colorScheme: ColorScheme ,
    file : String? ,
    name : String ,
    presenceText: String
    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically ,
        modifier = Modifier
            .padding(top = 4.dp , bottom = 4.dp)
    ) {
        Profile(
            modifier = Modifier.size(38.dp),
            profile = if (file != null) {
                {
                    Image(
                        painter = rememberAsyncImagePainter(file),
                        contentDescription = null ,
                        contentScale = ContentScale.Crop
                    )
                }
            } else null,
            color = colorScheme.background,
            firstCapitalLetterTxt = {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "?",
                    color = colorScheme.icon
                )
            }
        )
        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = presenceText ,
                style = MaterialTheme.typography.labelSmall,
                color = if (presenceText == "Online") {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
        }
    }
}


@Composable
fun ConversationActions(
    onVoiceCall : () -> Unit ,
    onVideoCall : () -> Unit
) {
    Row (
        verticalAlignment = Alignment.CenterVertically
        , modifier = Modifier
            .padding(end = 12.dp)
    ){
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onVoiceCall),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_call_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onVideoCall),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.video_call_svgrepo_com),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
