package com.thryvii.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thryvii.app.R
import com.thryvii.app.ui.theme.BackgroundGreen
import com.thryvii.app.ui.theme.CardCream
import com.thryvii.app.ui.theme.JuaFontFamily
import com.thryvii.app.ui.theme.SelectedPeach
import com.thryvii.app.ui.theme.TextBlack

private data class Seed(val iconRes: Int, val label: String)

private val seeds = listOf(
    Seed(R.drawable.seed_sunflower, "Sunflower"),
    Seed(R.drawable.seed_rose, "Rose"),
    Seed(R.drawable.seed_tulip, "Tulip")
)

/**
 * Maps 1:1 to the "Starter" frame in Figma (node 1:1065).
 *
 * Tapping the checkmark in the top bar -> [onCheckmarkClick] -> navigates to the Avatar page.
 */
@Composable
fun StarterScreen(
    onCheckmarkClick: (seed: String) -> Unit = {}
) {
    var selectedSeed by remember { mutableStateOf(seeds[0].label) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CardCream)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pick Your Starter Seed!",
                fontFamily = JuaFontFamily,
                fontSize = 24.sp,
                color = TextBlack
            )
            Text(
                text = "✓",
                fontFamily = JuaFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = TextBlack,
                modifier = Modifier.clickable { onCheckmarkClick(selectedSeed) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Seed choices
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            seeds.forEach { seed ->
                val isSelected = selectedSeed == seed.label
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SelectedPeach else CardCream)
                        .clickable { selectedSeed = seed.label },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(seed.iconRes),
                            contentDescription = seed.label,
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = seed.label,
                            fontFamily = JuaFontFamily,
                            fontSize = 20.sp,
                            color = TextBlack
                        )
                    }
                }
            }
        }
    }
}
