package com.thryvii.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thryvii.app.R
import com.thryvii.app.ui.theme.BackgroundGreen
import com.thryvii.app.ui.theme.CardCream
import com.thryvii.app.ui.theme.FieldBorderBlack
import com.thryvii.app.ui.theme.JuaFontFamily
import com.thryvii.app.ui.theme.NoteBrown
import com.thryvii.app.ui.theme.SelectedPeach
import com.thryvii.app.ui.theme.TextBlack

private data class Category(val iconRes: Int, val label: String)

private val categories = listOf(
    Category(R.drawable.tab_icon_hairstyle, "Hairstyle"),
    Category(R.drawable.tab_icon_shirt, "Shirt"),
    Category(R.drawable.tab_icon_pants, "Pants"),
    Category(R.drawable.tab_icon_shoes, "Shoes"),
    Category(R.drawable.tab_icon_accessory, "Accessory")
)

// Only "Hairstyle" has real exported option artwork so far.
private val hairstyleOptions = listOf(
    R.drawable.hairstyle_option_curly,
    R.drawable.hairstyle_option_bob,
    R.drawable.hairstyle_option_wavy,
    R.drawable.hairstyle_option_bun
)

private val swatchColors = listOf(
    Color(0xFF000000), Color(0xFF5C4033), Color(0xFFC1694F), Color(0xFFD9B48F),
    Color(0xFFE8B84B), Color(0xFF8FBF6B), Color(0xFF6FB3D9), Color(0xFFB98FD9),
    Color(0xFFE497B5), Color(0xFFA8A8A8)
)

/**
 * Maps 1:1 to the "Avatar" frame in Figma (node 1:990).
 *
 * Note: only the "Hairstyle" category has real exported option thumbnails so far (Shirt,
 * Pants, Shoes, Accessory show empty placeholder slots). Picking a hairstyle option doesn't
 * yet change the character preview artwork — that would need each option layered onto the
 * base character, which hasn't been exported/wired up yet.
 */
@Composable
fun AvatarScreen(
    onCheckmarkClick: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(categories[0].label) }
    var selectedColorIndex by remember { mutableStateOf(1) }
    var selectedOptionIndex by remember { mutableStateOf(2) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Character preview, exported from the Figma design.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.avatar_character),
                contentDescription = "Your avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(CardCream)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Your Avatar!",
                        fontFamily = JuaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = NoteBrown
                    )
                    Text(
                        text = "✓",
                        fontFamily = JuaFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = TextBlack,
                        modifier = Modifier.clickable(onClick = onCheckmarkClick)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category tabs
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category.label
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SelectedPeach else BackgroundGreen.copy(alpha = 0.25f))
                                .clickable { selectedCategory = category.label }
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(category.iconRes),
                                contentDescription = category.label,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = category.label,
                                fontFamily = JuaFontFamily,
                                fontSize = 15.sp,
                                color = TextBlack
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color swatches
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    swatchColors.forEachIndexed { index, color ->
                        val isSelected = selectedColorIndex == index
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = FieldBorderBlack,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Option thumbnails for the selected category — fills the remaining
                // vertical space in the card so there's no dead gap at the bottom.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedCategory == "Hairstyle") {
                        hairstyleOptions.forEachIndexed { index, optionRes ->
                            val isSelected = selectedOptionIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SelectedPeach else Color.White)
                                    .clickable { selectedOptionIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(optionRes),
                                    contentDescription = "Hairstyle option",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    } else {
                        // No exported option artwork yet for this category.
                        (0 until 4).forEach {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}
