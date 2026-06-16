package com.example.chatter.features.calls.presentation.call_history.compoenent

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.chatter.R
import com.example.chatter.core.presentation.Profile
import com.example.chatter.features.calls.presentation.call_history.model.Call


@Preview(device = Devices.PIXEL_C, showBackground = true)
@PreviewLightDark
@Composable
fun ACallPreview() {
    val call = dummyCallHistory.get(0)
    ACall(
        call = call
    )
}


@Composable
fun ACall(call : Call) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(15.dp) ,
        verticalAlignment = Alignment.CenterVertically ,
    ) {
        Profile(
            modifier = Modifier
                .padding(end = 10.dp)
                .size(70.dp),
            profile = call.image?.let {
                {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null ,
                        contentScale = ContentScale.Crop ,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            },
            color = call.colorScheme.background,
            firstCapitalLetterTxt = {
                Text(
                    text = call.name[0].uppercase() ,
                    color = call.colorScheme.icon
                )
            }
        )
        CallInfo(
            name = call.name,
            icon = call.statusIcon,
            callStatus = call.status
        )
        Spacer(modifier = Modifier.weight(1f))
        CallIcons()
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = call.timeStamp ,
            color = MaterialTheme.colorScheme.onSurface,)
    }
}


@Composable
fun CallInfo(
    name : String ,
    @DrawableRes
    icon : Int ,
    callStatus : String
) {
    Column {
        Text(
            text = name ,
            fontWeight = FontWeight.Bold ,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon) ,
                contentDescription = null ,
                modifier = Modifier
                    .size(15.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(0.4f),
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = callStatus ,
                fontSize = 12.sp ,
                color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
            )
        }
    }
}


@Composable
fun CallIcons() {
    Icon(
        painter = painterResource(R.drawable.outline_call_24) ,
        contentDescription = null ,
        modifier = Modifier.size(22.dp) ,
        tint = Color.Green
    )
    Spacer(modifier = Modifier.width(10.dp))
    Icon(
        painter = painterResource(R.drawable.video_call_svgrepo_com) ,
        contentDescription = null ,
        modifier = Modifier.size(22.dp) ,
        tint = Color.Green
    )
}
