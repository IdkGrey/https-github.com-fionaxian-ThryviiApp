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
import com.thryvii.app.R
import com.thryvii.app.ui.components.LabeledField
import com.thryvii.app.ui.components.PrimaryActionButton
import com.thryvii.app.ui.theme.BackgroundGreen
import com.thryvii.app.ui.theme.CardCream

/**
 * Maps 1:1 to the "Password" frame in Figma (node 1:941).
 *
 * [onSignUpClick] fires when "SIGN UP" is tapped — the Figma file doesn't specify what
 * comes after this step yet, so it's left as an open callback for you to wire up.
 */
@Composable
fun PasswordScreen(
    onSignUpClick: () -> Unit = {}
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
                    label = "PASSWORD",
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                LabeledField(
                    label = "CONFIRM PASSWORD",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(32.dp))
                PrimaryActionButton(
                    text = "SIGN UP",
                    onClick = onSignUpClick
                )
            }
        }
    }
}
