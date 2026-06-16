package com.example.chatter.features.auth.presentation.intro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatter.core.presentation.FilledButtonWithBlueBack
import com.example.chatter.ui.theme.blue400
import com.example.chatter.R


@Preview(device = Devices.PIXEL_C, showBackground = true)
@PreviewLightDark
@Composable
fun IntroScreenPreview() {
    IntroScreen({}) { }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(
    createAccount: () -> Unit ,
    signIn: () -> Unit
) {


    Scaffold(

    ){innerPadding ->
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 40.dp)
            ) {
                IntroTexts()
                Spacer(modifier = Modifier.weight(1f))
                val buttonModifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                IntroButtons(
                    buttonModifier,
                    createAccount = createAccount,
                    signIn = signIn,
                )
            }
        }
    }

}


@Composable
fun IntroTexts() {
    Text(
        text = "Chatter",
        modifier = Modifier,
        fontSize = 25.sp,
        color = blue400 // ✅ Uses primary theme color
    )
    Spacer(Modifier.height(30.dp))

    Text(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 45.sp,
                    color = MaterialTheme.colorScheme.onSurface // ✅ Text on surface background
                )
            ) {
                append("Connect\nWith Chatter ")
            }
            append("\n")

            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 45.sp,
                    color = blue400 // ✅ Tertiary for emphasis
                )
            ) {
                append("On the go!")
            }
        },
        lineHeight = 50.sp
    )
}

@Composable
fun IntroButtons(
    modifier: Modifier = Modifier,
    createAccount: () -> Unit,
    signIn: () -> Unit
) {
    FilledButtonWithBlueBack(
        modifier = modifier,
        text = stringResource(R.string.signup),
        elevation = 10.dp ,
        onClick = createAccount
    )

    Spacer(Modifier.height(20.dp))

    OutlinedButton(
        onClick = signIn,
        modifier = modifier,
        border = BorderStroke(2.dp, blue400) // ✅ Border uses primary
    ) {
        Text(stringResource(R.string.signin), fontSize = 18.sp, color = blue400) // ✅ Ensures text color matches theme
    }
}




