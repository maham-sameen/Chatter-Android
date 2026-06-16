package com.example.chatter.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.chatter.ui.theme.colorSchemes

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    profile : @Composable (() -> Unit)? = null,
    color: Color,
    firstCapitalLetterTxt: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .clip(CircleShape)
        ,
        shape = CircleShape,
        color = color.copy(0.8f) ,
    ) {
        if(profile != null){
            profile()
        }else{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // The letter
                firstCapitalLetterTxt()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactLogoPreview() {
    val (bg, icon) = colorSchemes.random()
    Profile(
        profile = {
            Image(
                painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/chatter-4e091.firebasestorage.app/o/image%2Fuser_image.jpg?alt=media&token=5584f49f-5c12-4a5f-881c-d1b5bffb6e56"),
                contentDescription = null ,
                contentScale = ContentScale.Crop ,
            )
        },
        modifier = Modifier.size(100.dp),
        color = bg,
        firstCapitalLetterTxt = {
            Text(
                text = "M",
                fontSize = 30.sp ,
                color = icon
            )
        }
    )
}
