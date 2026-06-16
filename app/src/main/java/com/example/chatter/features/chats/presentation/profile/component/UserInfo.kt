package com.example.chatter.features.chats.presentation.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatter.features.chats.presentation.profile.model.UserDetail
import kotlin.collections.iterator

@Composable
fun UserInfos(userDetails : SnapshotStateList<UserDetail>) {
    Column (
        modifier = Modifier.fillMaxWidth()
            .padding(start = 30.dp) ,
    ){
        for ( i in userDetails.iterator()){
            Spacer(modifier = Modifier.height(30.dp))
            UserInfo(i)
        }
    }
}


@Composable
fun UserInfo(userDetail: UserDetail) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(userDetail.icon) ,
            contentDescription = null ,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.width(25.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = userDetail.name
                , color = MaterialTheme.colorScheme.onSurface ,
                fontSize = 17.sp ,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = userDetail.value ,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
        }
    }
}

