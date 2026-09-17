package com.thryvii.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thryvii.app.ui.theme.ButtonRed
import com.thryvii.app.ui.theme.ButtonTextCream
import com.thryvii.app.ui.theme.FieldBackgroundWhite
import com.thryvii.app.ui.theme.FieldBorderBlack
import com.thryvii.app.ui.theme.JuaFontFamily
import com.thryvii.app.ui.theme.TextBlack
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.thryvii.app.ui.theme.CardCream

/**
 * Red, rounded, black-bordered call-to-action button — matches "CREATE ACCOUNT",
 * "CONTINUE" and "LOG IN" in the Figma design.
 */
@Composable
fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillWidth: Boolean = true,
    height: Dp = 49.dp,
    fontSize: TextUnit = 24.sp,
    horizontalContentPadding: Dp = 0.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonRed,
            contentColor = ButtonTextCream
        ),
        border = BorderStroke(1.dp, FieldBorderBlack),
        contentPadding = PaddingValues(horizontal = horizontalContentPadding)
    ) {
        Text(
            text = text,
            fontFamily = JuaFontFamily,
            fontSize = fontSize
        )
    }
}

/**
 * Labeled input field — white background, black border, rounded corners — matches the
 * EMAIL / Username / PASSWORD fields in the Figma design.
 */
@Composable
fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TextBlack,
            fontFamily = JuaFontFamily,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(49.dp),
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(fontFamily = JuaFontFamily, fontSize = 16.sp),
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldBackgroundWhite,
                unfocusedContainerColor = FieldBackgroundWhite,
                focusedBorderColor = FieldBorderBlack,
                unfocusedBorderColor = FieldBorderBlack
            ),
            singleLine = true
        )
    }
}

/**
 * Fixed-size square icon+label card used for the game-screen quick-action buttons (the
 * right-side rail and the bottom action row on Main / Edit Garden). Uniform size regardless
 * of label length.
 */
@Composable
fun SquareIconCard(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 68.dp,
    containerColor: androidx.compose.ui.graphics.Color = CardCream,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .then(Modifier.clickable(onClick = onClick))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontFamily = JuaFontFamily,
            fontSize = 15.sp,
            color = TextBlack,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
