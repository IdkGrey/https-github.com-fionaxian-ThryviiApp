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
import com.thryvii.app.ui.theme.NoteBrown
import com.thryvii.app.ui.theme.TextBlack

/**
 * Maps 1:1 to the "Make Garden" frame in Figma (node 1:972).
 *
 * [onStartGardenClick] fires when "START MY GARDEN!" is tapped — the Figma file doesn't
 * specify what comes after this step yet, so it's left as an open callback for you to wire up.
 */
@Composable
fun MakeGardenScreen(
    onStartGardenClick: (gardenName: String, username: String, trackLocation: Boolean) -> Unit = { _, _, _ -> }
) {
    var gardenName by remember { mutableStateOf("My Sprout Land") }
    var username by remember { mutableStateOf("Sunny_Sprout") }
    var trackLocation by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Landscape illustration, exported from the Figma design.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.make_garden_illustration),
                contentDescription = "Garden landscape illustration",
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
                    label = "GARDEN NAME",
                    value = gardenName,
                    onValueChange = { gardenName = it }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: you can change this later",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown
                )
                Spacer(modifier = Modifier.height(16.dp))
                LabeledField(
                    label = "USERNAME",
                    value = username,
                    onValueChange = { username = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = trackLocation,
                        onCheckedChange = { trackLocation = it },
                        colors = CheckboxDefaults.colors(checkedColor = ButtonRed)
                    )
                    Text(
                        text = "Allow App to Track Location",
                        fontFamily = JuaFontFamily,
                        fontSize = 16.sp,
                        color = TextBlack
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                PrimaryActionButton(
                    text = "START MY GARDEN!",
                    onClick = { onStartGardenClick(gardenName, username, trackLocation) }
                )
            }
        }
    }
}
