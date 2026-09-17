package com.thryvii.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thryvii.app.R
import com.thryvii.app.ui.components.PrimaryActionButton
import com.thryvii.app.ui.theme.BackgroundGreen
import com.thryvii.app.ui.theme.CardCream
import com.thryvii.app.ui.theme.JuaFontFamily
import com.thryvii.app.ui.theme.TextBlack

/**
 * Maps 1:1 to the "Welcome" frame in Figma (node 1:921).
 *
 *  - Tapping "CREATE ACCOUNT" (node 1:926) -> [onCreateAccountClick] -> navigates to the Email screen
 *  - Tapping "LOGIN" (node 1:3660)          -> [onLoginClick]         -> navigates to the Sign In screen
 */
@Composable
fun WelcomeScreen(
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Cream card: logo (pinned top-left), headline, CTA (centered)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(CardCream, RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Logo pinned to the top-left corner of the card, on its own row above the
                // centered content below - previously it was absolutely-positioned over the
                // same vertically-centered Column as "WELCOME!", so on this app's short
                // landscape screens the two ended up overlapping in the same spot.
                Image(
                    painter = painterResource(R.drawable.thryvii_logo),
                    contentDescription = "Thryvii logo",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .size(96.dp)
                )

                // Two equal-weight spacers center this group in the space left BELOW the logo,
                // instead of across the card's full height.
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "WELCOME!",
                    fontFamily = JuaFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Let's get started!",
                    fontFamily = JuaFontFamily,
                    fontSize = 24.sp,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(40.dp))
                PrimaryActionButton(
                    text = "CREATE ACCOUNT",
                    onClick = onCreateAccountClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                LoginPrompt(onLoginClick = onLoginClick)

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Character illustration, exported from the Figma design — fills the green pane.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.welcome_illustration),
                contentDescription = "Farmer character illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun LoginPrompt(onLoginClick: () -> Unit) {
    val annotated = buildAnnotatedString {
        append("Already Have an Account? ")
        pushStringAnnotation(tag = "LOGIN", annotation = "login")
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
            append("LOGIN")
        }
        pop()
    }

    ClickableText(
        text = annotated,
        style = androidx.compose.ui.text.TextStyle(
            fontFamily = JuaFontFamily,
            fontSize = 16.sp,
            color = TextBlack
        ),
        onClick = { offset ->
            annotated.getStringAnnotations(tag = "LOGIN", start = offset, end = offset)
                .firstOrNull()?.let { onLoginClick() }
        }
    )
}
