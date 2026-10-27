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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thryvii.app.R
import com.thryvii.app.ui.components.LabeledField
import com.thryvii.app.ui.components.PrimaryActionButton
import com.thryvii.app.ui.theme.BackgroundGreen
import com.thryvii.app.ui.theme.ButtonRed
import com.thryvii.app.ui.theme.CardCream
import com.thryvii.app.ui.theme.JuaFontFamily
import com.thryvii.app.ui.theme.TextBlack

/**
 * Maps 1:1 to the "Sign In" frame in Figma (node 1:955).
 *
 * [onLoginClick] fires when "LOG IN" is tapped — not specified in the request, so it's left
 * as an open callback (e.g. to validate credentials and navigate into the app).
 */
@Composable
fun SignInScreen(
    onLoginClick: (usernameOrEmail: String) -> Unit = {}
) {
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Character illustration, exported from the Figma design.
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

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(CardCream, RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                LabeledField(
                    label = "Username or Email",
                    value = usernameOrEmail,
                    onValueChange = { usernameOrEmail = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LabeledField(
                    label = "PASSWORD",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = ButtonRed)
                    )
                    Text(
                        text = "Remember me",
                        fontFamily = JuaFontFamily,
                        fontSize = 16.sp,
                        color = TextBlack
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                PrimaryActionButton(
                    text = "LOG IN",
                    onClick = { onLoginClick(usernameOrEmail) }
                )
            }
        }
    }
}
