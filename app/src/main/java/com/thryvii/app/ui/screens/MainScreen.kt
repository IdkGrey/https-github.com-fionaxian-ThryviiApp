package com.thryvii.app.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.TextStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.thryvii.app.R
import com.thryvii.app.ui.components.SquareIconCard
import com.thryvii.app.ui.theme.BackgroundGreen
import com.thryvii.app.ui.theme.ButtonRed
import com.thryvii.app.ui.theme.ButtonTextCream
import com.thryvii.app.ui.theme.CardCream
import com.thryvii.app.ui.theme.FieldBorderBlack
import com.thryvii.app.ui.theme.JuaFontFamily
import com.thryvii.app.ui.theme.NoteBrown
import com.thryvii.app.ui.theme.PriceBorderGray
import com.thryvii.app.ui.theme.SelectedPeach
import com.thryvii.app.ui.theme.TextBlack
import com.thryvii.app.ui.theme.ScrimBlack
import com.thryvii.app.ui.theme.LogoGreen
import com.thryvii.app.ui.theme.DirtBrown
import com.thryvii.app.ui.components.PrimaryActionButton
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as lazyColumnItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import kotlinx.coroutines.delay

/**
 * Main / home screen (Figma nodes 1:1082 "Main" and 1:1260 "Edit" - the two share the exact
 * same background and top bar in the real design, so "Edit" is implemented here as a toggled
 * mode on Main rather than a separate screen).
 *
 * Three visual states, all crossfaded:
 *  - Normal: plain background, Weather/Journal/Quote rail.
 *  - Edit mode (Edit Garden pressed): grid-overlay background, Decorate/Plant rail, Edit
 *    Garden button highlighted peach.
 *  - Watering can selected (tapped while in edit mode): background swaps to the
 *    highlighted-watering-can art, rail swaps to Move/Delete.
 *
 * The watering-can tap target is an approximate hotspot over its position in the background
 * art - it lives inside the same scaled/shifted layer as the image so it tracks the art, but
 * the exact position may need a nudge once you've tried it on-device.
 */
@Composable
fun MainScreen(onAvatarClick: () -> Unit = {}) {
    val density = LocalDensity.current
    var isEditMode by remember { mutableStateOf(false) }
    var isWateringCanSelected by remember { mutableStateOf(false) }
    var showQuoteDialog by remember { mutableStateOf(false) }
    var showSaveQuoteDialog by remember { mutableStateOf(false) }
    var showWeatherDialog by remember { mutableStateOf(false) }
    var showShopScreen by remember { mutableStateOf(false) }
    var showNotificationsScreen by remember { mutableStateOf(false) }
    var showVisitGardensScreen by remember { mutableStateOf(false) }
    var showQuestsScreen by remember { mutableStateOf(false) }
    var showJournalScreen by remember { mutableStateOf(false) }
    var showAddDecorationScreen by remember { mutableStateOf(false) }
    var showAddPlantScreen by remember { mutableStateOf(false) }
    var showSettingsPanel by remember { mutableStateOf(false) }
    var visitTarget by remember { mutableStateOf<VisitTarget?>(null) }

    // "Traveling to garden..." / "Returning to garden..." transition - a brown full-screen
    // overlay with an animated "..." shown whenever a Visit or Leave action is about to swap
    // screens. The actual state change (setting/clearing visitTarget) is deferred until the
    // animated hold finishes, via startTravelTransition below; the overlay then fades out
    // (rather than disappearing instantly) to reveal the already-swapped screen underneath.
    var isTraveling by remember { mutableStateOf(false) }
    var showTravelOverlay by remember { mutableStateOf(false) }
    var pendingTravelAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var travelDotCount by remember { mutableStateOf(1) }
    var travelLabel by remember { mutableStateOf("Traveling to garden") }

    LaunchedEffect(isTraveling) {
        if (isTraveling) {
            travelDotCount = 1
            // ~2 seconds of animated "..." before swapping screens underneath the overlay.
            val stepMs = 330L
            val steps = 6
            repeat(steps) {
                delay(stepMs)
                travelDotCount = (travelDotCount % 3) + 1
            }
            pendingTravelAction?.invoke()
            pendingTravelAction = null
            isTraveling = false
            // Reveal the new screen with a fade instead of an instant cut.
            showTravelOverlay = false
        }
    }

    fun startTravelTransition(label: String, action: () -> Unit) {
        travelLabel = label
        pendingTravelAction = action
        showTravelOverlay = true
        isTraveling = true
    }

    val editGardenColor by animateColorAsState(
        targetValue = if (isEditMode) SelectedPeach else CardCream,
        label = "edit-garden-highlight"
    )

    val backgroundRes = when {
        isWateringCanSelected -> R.drawable.main_background_watering_can
        isEditMode -> R.drawable.main_background_edit
        else -> R.drawable.main_background
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background is scaled up slightly and nudged upward (then clipped) so the sprout
        // plot reads as the focal point instead of sitting low in the frame on tall screens.
        // The watering-can hotspot lives inside this same transformed layer so its tap
        // target tracks the art through the scale/shift.
        //
        // The scale has to be large enough to cover the upward shift on every side, or the
        // image pulls away from the bottom edge and exposes the app theme's plain green
        // windowBackground underneath as a strip - which is exactly what was happening at
        // 1.22x/-70dp on this screen's actual height. 1.4x/-55dp keeps a comfortable safety
        // margin so the art always fully covers the box regardless of the exact device height.
        Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.4f
                        scaleY = 1.4f
                        translationY = with(density) { (-55).dp.toPx() }
                    }
            ) {
                Crossfade(targetState = backgroundRes, label = "background-crossfade") { res ->
                    Image(
                        painter = painterResource(res),
                        contentDescription = "Garden background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Approximate hotspot over the watering can - only active in edit mode.
                if (isEditMode) {
                    Box(
                        modifier = Modifier
                            .align(BiasAlignment(horizontalBias = 0.12f, verticalBias = 0.38f))
                            .size(120.dp)
                            .clickable { isWateringCanSelected = !isWateringCanSelected }
                    )
                }
            }
        }

        // Everything below is foreground UI, inset from the camera cutout (left edge) and the
        // status bar / battery-wifi-time area (top edge) so nothing sits under or crosses
        // them - the background layer above stays edge-to-edge on purpose.
        Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

        // Top-left: garden name pill + notification bell, side by side
        Row(
            modifier = Modifier.align(Alignment.TopStart).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(CardCream)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_menu),
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { showSettingsPanel = true }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "My Sprout Land", fontFamily = JuaFontFamily, fontSize = 16.sp, color = TextBlack)
            }

            Box {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(CardCream)
                        .clickable { showNotificationsScreen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_bell),
                        contentDescription = "Notifications",
                        modifier = Modifier.size(28.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(ButtonRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "2", fontFamily = JuaFontFamily, fontSize = 15.sp, color = Color.White)
                }
            }
        }

        // Top-right currency badge
        Image(
            painter = painterResource(R.drawable.icon_coin_badge),
            contentDescription = "10 coins",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .height(52.dp),
            contentScale = ContentScale.FillHeight
        )

        // Right side rail: Weather/Journal/Quote normally, Decorate/Plant while editing,
        // Move/Delete while the watering can is selected. Crossfades between all three.
        Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 40.dp)) {
            Crossfade(
                targetState = Pair(isEditMode, isWateringCanSelected),
                label = "side-rail-crossfade"
            ) { (editing, canSelected) ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when {
                        canSelected -> {
                            SquareIconCard(iconRes = R.drawable.icon_move, label = "Move")
                            SquareIconCard(iconRes = R.drawable.icon_delete, label = "Delete")
                        }
                        editing -> {
                            SquareIconCard(
                                iconRes = R.drawable.icon_decorate,
                                label = "Decorate",
                                onClick = { showAddDecorationScreen = true }
                            )
                            SquareIconCard(
                                iconRes = R.drawable.icon_plant,
                                label = "Plant",
                                onClick = { showAddPlantScreen = true }
                            )
                        }
                        else -> {
                            SquareIconCard(
                                iconRes = R.drawable.icon_weather,
                                label = "Weather",
                                onClick = { showWeatherDialog = true }
                            )
                            SquareIconCard(
                                iconRes = R.drawable.icon_journal,
                                label = "Journal",
                                onClick = { showJournalScreen = true }
                            )
                            SquareIconCard(
                                iconRes = R.drawable.icon_quote,
                                label = "Quote",
                                onClick = { showQuoteDialog = true }
                            )
                        }
                    }
                }
            }
        }

        // Bottom-center: Shop / Quests / Edit Garden / Visit - uniform squares, close together.
        // Edit Garden highlights peach while active, matching the Figma "Edit" state.
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SquareIconCard(
                iconRes = R.drawable.icon_shop,
                label = "Shop",
                onClick = { showShopScreen = true }
            )
            SquareIconCard(
                iconRes = R.drawable.icon_quests,
                label = "Quests",
                onClick = { showQuestsScreen = true }
            )
            SquareIconCard(
                iconRes = R.drawable.icon_edit,
                label = "Edit",
                containerColor = editGardenColor,
                onClick = {
                    if (isEditMode) {
                        isEditMode = false
                        isWateringCanSelected = false
                    } else {
                        isEditMode = true
                    }
                }
            )
            SquareIconCard(
                iconRes = R.drawable.icon_people,
                label = "Visit",
                onClick = { showVisitGardensScreen = true }
            )
        }

        }

        // "Quote of the Day" modal - matches Figma node 1:1197 (dims the screen, centers a card).
        // Kept outside the safe-drawing-inset wrapper above so its scrim dims the WHOLE phone
        // (camera cutout and status/gesture bars included), not just the inset content area.
        if (showQuoteDialog) {
            QuoteOfTheDayDialog(
                onDismiss = { showQuoteDialog = false },
                onSaveQuote = {
                    showQuoteDialog = false
                    showSaveQuoteDialog = true
                }
            )
        }

        // "Save Quote" confirmation modal - matches Figma node 1:1137
        if (showSaveQuoteDialog) {
            SaveQuoteDialog(onDismiss = { showSaveQuoteDialog = false })
        }

        // "Weather" modal - matches Figma node 1:2041
        if (showWeatherDialog) {
            WeatherDialog(onDismiss = { showWeatherDialog = false })
        }

        // "Settings" panel - matches the real Figma "Settings" frame: tapping the hamburger
        // menu dims the whole screen and slides a left-anchored panel over it (profile photo +
        // name/location, then Avatar/Smart Watch Connection/Settings/Help Center rows), rather
        // than navigating to a separate full page. The scrim and the panel are two independent
        // AnimatedVisibility blocks (both stable APIs, not the experimental nested variant) so
        // the scrim fades while the panel slides in/out from the left edge, instead of either
        // one appearing/disappearing instantly.
        AnimatedVisibility(
            visible = showSettingsPanel,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScrimBlack)
                    .clickable { showSettingsPanel = false }
            )
        }

        AnimatedVisibility(
            visible = showSettingsPanel,
            modifier = Modifier.align(Alignment.CenterStart),
            enter = slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it }),
            exit = slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it })
        ) {
            SettingsPanel(
                onDismiss = { showSettingsPanel = false },
                onAvatarClick = {
                    showSettingsPanel = false
                    onAvatarClick()
                }
            )
        }

        // "Shop" screen - matches Figma node 1:1410 "Shop Mystery Bags". Covers the whole
        // screen (not a scrim dialog like the ones above) since the real design is its own
        // full page with a green header/tab bar and a peach content area.
        if (showShopScreen) {
            ShopScreen(onClose = { showShopScreen = false })
        }

        // "Notifications" screen - matches the real Figma "Notifications" frame. Full-screen
        // like Shop, with a New Notifications column (fixed) and a Past Notifications column
        // (scrollable) side by side, divided by a vertical line.
        if (showNotificationsScreen) {
            NotificationsScreen(
                onClose = { showNotificationsScreen = false },
                onVisit = { item ->
                    startTravelTransition("Traveling to garden") {
                        visitTarget = VisitTarget(item.gardenName, item.username, VisitOrigin.NOTIFICATIONS)
                    }
                }
            )
        }

        // "Visit Gardens" screen - matches the real Figma "Visit Gardens" frame. Same
        // full-screen two-column pattern as Notifications: "This Week's Showcase" (fixed)
        // and "Other Gardens" (scrollable) side by side, divided by a vertical line.
        if (showVisitGardensScreen) {
            VisitGardensScreen(
                onClose = { showVisitGardensScreen = false },
                onVisit = { item ->
                    startTravelTransition("Traveling to garden") {
                        visitTarget = VisitTarget(item.gardenName, item.username, VisitOrigin.VISIT_GARDENS)
                    }
                }
            )
        }

        // "Quests" screen - matches the real Figma "Quests" frame. Same full-screen
        // two-column pattern as Notifications/Visit Gardens: "Weather Special Quests"
        // (fixed) and "Daily Quests" (scrollable) side by side, divided by a vertical line.
        if (showQuestsScreen) {
            QuestsScreen(onClose = { showQuestsScreen = false })
        }

        // "Journal" screen - matches the real Figma "Journal" frame. Unlike the other full
        // screens, this one is authored upright in Figma (no page-level rotation), so its
        // coordinates map directly: a peach page background, a centered "MY JOURNAL" title
        // pill, a fixed stack of recent-entry cards, and a floating "CREATE NEW" button.
        if (showJournalScreen) {
            JournalScreen(onClose = { showJournalScreen = false })
        }

        // "Add Decoration" screen - matches the real Figma "Add Decoration" frame.
        if (showAddDecorationScreen) {
            AddItemScreen(
                title = "Select Decoration to Add:",
                items = decorationOptions,
                confirmLabel = "ADD DECORATION",
                badgeBorderColor = NoteBrown,
                onClose = { showAddDecorationScreen = false }
            )
        }

        // "Add Plant" screen - matches the real Figma "Add Plant" frame.
        if (showAddPlantScreen) {
            AddItemScreen(
                title = "Select Plant to Add:",
                items = plantOptions,
                confirmLabel = "ADD PLANT",
                badgeBorderColor = PriceBorderGray,
                onClose = { showAddPlantScreen = false }
            )
        }

        // "Visiting a Garden" screen - overlays whichever of Notifications/Visit Gardens the
        // visit was launched from (that screen is still open underneath, just covered). Leave
        // clears visitTarget AND closes whichever list screen is underneath, so it always lands
        // back on the plain main garden screen rather than back on that list.
        val currentVisitTarget = visitTarget
        if (currentVisitTarget != null) {
            VisitingGardenScreen(
                target = currentVisitTarget,
                onLeave = {
                    startTravelTransition("Returning to garden") {
                        visitTarget = null
                        showNotificationsScreen = false
                        showVisitGardensScreen = false
                    }
                }
            )
        }

        // "Traveling to garden..." / "Returning to garden..." overlay - rendered last so it
        // covers whichever screen is currently up (Notifications/Visit Gardens/the visiting
        // screen itself), including the camera cutout and status/gesture bar areas. It fades
        // out (rather than vanishing instantly) once the destination screen is ready underneath.
        AnimatedVisibility(
            visible = showTravelOverlay,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(600))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DirtBrown),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = travelLabel + ".".repeat(travelDotCount),
                    fontFamily = JuaFontFamily,
                    fontSize = 22.sp,
                    color = ButtonTextCream
                )
            }
        }
    }
}

@Composable
private fun WeatherDialog(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScrimBlack)
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 220.dp)
                .clickable { } // no-op click - blocks taps inside the card from reaching the scrim below
                .clip(RoundedCornerShape(10.dp))
                .background(CardCream)
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Weather (Rainy)", fontFamily = JuaFontFamily, fontSize = 16.sp, color = TextBlack)
                Text(
                    text = "✕",
                    fontSize = 16.sp,
                    color = TextBlack,
                    modifier = Modifier.clickable(onClick = onDismiss)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(R.drawable.icon_weather_rainy),
                contentDescription = "Rainy",
                modifier = Modifier
                    .heightIn(max = 150.dp)
                    .aspectRatio(760f / 748f, matchHeightConstraintsFirst = true),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Plants will grow 2x as fast!",
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = TextBlack,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuoteOfTheDayDialog(
    onDismiss: () -> Unit,
    onSaveQuote: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScrimBlack)
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clickable { } // no-op click - blocks taps inside the card from reaching the scrim below
                .clip(RoundedCornerShape(10.dp))
                .background(CardCream)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Quote of the Day!", fontFamily = JuaFontFamily, fontSize = 17.sp, color = TextBlack)
                Text(
                    text = "✕",
                    fontSize = 15.sp,
                    color = TextBlack,
                    modifier = Modifier.clickable(onClick = onDismiss)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Real exported quote art (texture + text + attribution baked in from Figma).
            // Height is capped directly (not derived from aspectRatio) since this app's
            // viewport is short in landscape - a width-driven aspect ratio made this image
            // taller than the screen and pushed the title/button off both edges.
            Image(
                painter = painterResource(R.drawable.quote_card_art),
                contentDescription = "Quote of the day",
                modifier = Modifier
                    .heightIn(max = 190.dp)
                    .aspectRatio(1224f / 1068f, matchHeightConstraintsFirst = true)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(10.dp))
            PrimaryActionButton(
                text = "SAVE QUOTE",
                onClick = onSaveQuote,
                fillWidth = false,
                height = 34.dp,
                fontSize = 15.sp,
                horizontalContentPadding = 20.dp
            )
        }
    }
}

@Composable
private fun SaveQuoteDialog(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScrimBlack)
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .clickable { } // no-op click - blocks taps inside the card from reaching the scrim below
                .clip(RoundedCornerShape(10.dp))
                .background(CardCream)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Successfully Saved!", fontFamily = JuaFontFamily, fontSize = 16.sp, color = TextBlack)
                Text(
                    text = "✕",
                    fontSize = 15.sp,
                    color = TextBlack,
                    modifier = Modifier.clickable(onClick = onDismiss)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Height capped directly for the same reason as the Quote of the Day dialog -
            // see the comment there.
            Image(
                painter = painterResource(R.drawable.quote_card_art),
                contentDescription = "Saved quote",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .heightIn(max = 160.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * "Settings" panel - matches the real Figma "Settings" frame (a dimmed main screen with a
 * left-anchored panel sliding over it, not a separate full page). The panel's cream background
 * fills all the way to the phone's true left/top/bottom edges (same "let the color reach the
 * edge" treatment as the Shop screen); only its content is inset from the camera cutout and
 * status/gesture bars. The scrim (fade) and this panel's slide-in/out are handled by the two
 * sibling AnimatedVisibility blocks at the call site - this composable is just the panel itself.
 */
@Composable
private fun SettingsPanel(onDismiss: () -> Unit, onAvatarClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.526f)
                .shadow(8.dp)
                .background(CardCream)
                .clickable(enabled = false) {} // no-op click - blocks taps inside the panel from reaching the scrim below
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Start + WindowInsetsSides.Top + WindowInsetsSides.Bottom
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                // Close X - top-right of the panel
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "✕",
                        fontFamily = JuaFontFamily,
                        fontSize = 22.sp,
                        color = TextBlack,
                        modifier = Modifier.align(Alignment.TopEnd).clickable(onClick = onDismiss)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Profile photo + username + location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.avatar_sprout),
                            contentDescription = "Sunny_Sprout",
                            modifier = Modifier.size(60.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Sunny_Sprout", fontFamily = JuaFontFamily, fontSize = 20.sp, color = TextBlack)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Seattle, WA", fontFamily = JuaFontFamily, fontSize = 15.sp, color = NoteBrown)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                SettingsRow(icon = Icons.Filled.Person, label = "Avatar", onClick = onAvatarClick)
                Spacer(modifier = Modifier.height(12.dp))
                SettingsRow(icon = Icons.Outlined.Watch, label = "Smart Watch Connection", onClick = {})
                Spacer(modifier = Modifier.height(12.dp))
                SettingsRow(icon = Icons.Outlined.Settings, label = "Settings", onClick = {})
                Spacer(modifier = Modifier.height(12.dp))
                SettingsRow(icon = Icons.Outlined.Help, label = "Help Center", onClick = {})
            }
        }
}

/**
 * One icon+label row inside the Settings panel (Avatar / Smart Watch Connection / etc). The
 * whole row's background darkens slightly on mouse hover.
 */
@Composable
private fun SettingsRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val rowBackground by animateColorAsState(
        targetValue = if (isHovered) SelectedPeach else Color.Transparent,
        label = "settings-row-hover"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBackground)
            .hoverable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Black,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, fontFamily = JuaFontFamily, fontSize = 16.sp, color = TextBlack)
    }
}

/**
 * Categories on the Shop screen (Figma node 1:1410 "Shop Mystery Bags"). Only Mystery Bags is
 * populated for now - Plants and Decorations are stubbed with a "Coming soon" placeholder.
 */
private enum class ShopTab(val label: String) {
    MYSTERY_BAGS("Mystery Bags"),
    PLANTS("Plants"),
    DECORATIONS("Decorations")
}

/**
 * "Shop" screen - matches Figma node 1:1410 "Shop Mystery Bags". Unlike Quote/Weather/Save,
 * this is a full page in the real design (its own green header + red tab bar + peach content
 * area) rather than a small centered card, so it's rendered as an opaque full-screen overlay.
 */
@Composable
private fun ShopScreen(onClose: () -> Unit) {
    var selectedTab by remember { mutableStateOf(ShopTab.MYSTERY_BAGS) }

    // No safe-drawing inset anywhere in this screen - the green header, red tabs, and peach
    // content all fill edge-to-edge with no padding, so there's no gap anywhere for the green
    // background behind them to show through as a strip.
    Box(modifier = Modifier.fillMaxSize().background(BackgroundGreen)) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header: coin badge, "SHOP" title, close X
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_coin_badge),
                contentDescription = "10 coins",
                modifier = Modifier.align(Alignment.CenterStart).height(40.dp),
                contentScale = ContentScale.FillHeight
            )
            Text(
                text = "SHOP",
                fontFamily = JuaFontFamily,
                fontSize = 26.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "✕",
                fontFamily = JuaFontFamily,
                fontSize = 22.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.CenterEnd).clickable(onClick = onClose)
            )
        }

        // Category tabs
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShopTab.values().forEach { tab ->
                val selected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(ButtonRed)
                        .border(
                            width = 3.dp,
                            color = if (selected) CardCream else Color.Black,
                            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.label,
                        fontFamily = JuaFontFamily,
                        fontSize = 15.sp,
                        color = CardCream,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Content area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(SelectedPeach)
        ) {
            when (selectedTab) {
                ShopTab.MYSTERY_BAGS -> {
                    // Same grid (7 columns, 6dp spacing, 40dp side padding) and same card size
                    // as Plants/Decorations, so all 3 tabs' boxes line up identically - just with
                    // only 4 items, so they land in the first row's first 4 slots.
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 40.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(mysteryBagShopItems) { (iconRes, label) ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                MysteryBagCard(
                                    iconRes = iconRes,
                                    label = label,
                                    price = 10,
                                    cardWidth = 106.dp,
                                    cardHeight = 140.dp,
                                    cardPadding = 8.dp,
                                    // Mystery Bags keeps its own confirmed border colors
                                    // (NoteBrown, the defaults) - only the size/spacing/grid
                                    // position matches Plants/Decorations, not their black/gray
                                    // borders.
                                    iconSize = 68.dp,
                                    labelFontSize = 15.sp,
                                    labelBoxHeight = 22.dp,
                                    spacerHeight = 4.dp,
                                    priceCoinSize = 18.dp,
                                    priceFontSize = 15.sp,
                                    priceHorizontalPadding = 8.dp,
                                    priceVerticalPadding = 3.dp,
                                    priceSpacing = 4.dp,
                                    priceTextColor = NoteBrown
                                )
                            }
                        }
                    }
                }
                ShopTab.PLANTS -> {
                    // Matches the real "Shop Plants" Figma frame: a 7-column x 3-row (21 slot)
                    // grid that scrolls vertically, item cards ~109x143px with a black 1px
                    // border (not the Mystery Bag cards' brown), and a price pill with a gray
                    // #9A9A9A border instead of brown. Fixed(7), not Adaptive, so it's always
                    // exactly 7 per row. Extra side padding (on top of the content area's own
                    // 16dp) keeps slots off the screen edges.
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 40.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(plantShopItems) { (iconRes, label) ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                MysteryBagCard(
                                    iconRes = iconRes,
                                    label = label,
                                    price = 10,
                                    cardWidth = 106.dp,
                                    cardHeight = 140.dp,
                                    cardPadding = 8.dp,
                                    cardBorderColor = FieldBorderBlack,
                                    iconSize = 68.dp,
                                    labelFontSize = 15.sp,
                                    labelBoxHeight = 22.dp,
                                    spacerHeight = 4.dp,
                                    priceCoinSize = 18.dp,
                                    priceFontSize = 15.sp,
                                    priceHorizontalPadding = 8.dp,
                                    priceVerticalPadding = 3.dp,
                                    priceSpacing = 4.dp,
                                    priceBorderColor = PriceBorderGray
                                )
                            }
                        }
                    }
                }
                ShopTab.DECORATIONS -> {
                    // Matches the real "Shop Decorations" Figma frame (Frame 4): the same
                    // 7-column x 3-row scrollable grid and card style as Plants. Only 17 of the
                    // 21 items have arrived so far (the last row's final 4 columns weren't in
                    // the CSS/image set yet), so those render as empty placeholder cards.
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 40.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(decorationShopItems) { item ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item != null) {
                                    val (iconRes, label) = item
                                    MysteryBagCard(
                                        iconRes = iconRes,
                                        label = label,
                                        price = 10,
                                        cardWidth = 106.dp,
                                        cardHeight = 140.dp,
                                        cardPadding = 8.dp,
                                        cardBorderColor = FieldBorderBlack,
                                        iconSize = 68.dp,
                                        labelFontSize = 15.sp,
                                        labelBoxHeight = 22.dp,
                                        spacerHeight = 4.dp,
                                        priceCoinSize = 18.dp,
                                        priceFontSize = 15.sp,
                                        priceHorizontalPadding = 8.dp,
                                        priceVerticalPadding = 3.dp,
                                        priceSpacing = 4.dp,
                                        priceBorderColor = PriceBorderGray
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .width(106.dp)
                                            .height(140.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(CardCream)
                                            .border(1.dp, FieldBorderBlack, RoundedCornerShape(12.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
}

/**
 * One purchasable item card on the Shop screen - label, bag art, and a coin-and-price tag.
 * Fixed width AND height so every card lines up the same regardless of label length or
 * image proportions.
 */
@Composable
private fun MysteryBagCard(
    iconRes: Int,
    label: String,
    price: Int,
    cardWidth: Dp = 132.dp,
    cardHeight: Dp = 200.dp,
    cardPadding: Dp = 10.dp,
    cardBorderColor: Color = NoteBrown,
    iconSize: Dp = 104.dp,
    labelFontSize: TextUnit = 15.sp,
    labelBoxHeight: Dp = 34.dp,
    spacerHeight: Dp = 8.dp,
    priceCoinSize: Dp = 22.dp,
    priceFontSize: TextUnit = 15.sp,
    priceHorizontalPadding: Dp = 10.dp,
    priceVerticalPadding: Dp = 4.dp,
    priceSpacing: Dp = 6.dp,
    priceBorderColor: Color = NoteBrown,
    priceTextColor: Color = TextBlack
) {
    Column(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(CardCream)
            .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp))
            .padding(cardPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(labelBoxHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontFamily = JuaFontFamily,
                fontSize = labelFontSize,
                color = TextBlack,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.height(spacerHeight))
        Image(
            painter = painterResource(iconRes),
            contentDescription = label,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.height(spacerHeight))
        PriceTag(
            price = price,
            coinSize = priceCoinSize,
            fontSize = priceFontSize,
            horizontalPadding = priceHorizontalPadding,
            verticalPadding = priceVerticalPadding,
            spacing = priceSpacing,
            borderColor = priceBorderColor,
            textColor = priceTextColor
        )
    }
}

/**
 * A coin icon and the price number together inside one rounded pill (not just the number).
 * Sizing is parameterized so the same composable works full-size on the Mystery Bag cards and
 * shrunk down on the dense Plants grid.
 */
@Composable
private fun PriceTag(
    price: Int,
    modifier: Modifier = Modifier,
    coinSize: Dp = 22.dp,
    fontSize: TextUnit = 15.sp,
    horizontalPadding: Dp = 10.dp,
    verticalPadding: Dp = 4.dp,
    spacing: Dp = 6.dp,
    borderColor: Color = NoteBrown,
    textColor: Color = TextBlack
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(CardCream)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.icon_coin),
            contentDescription = "coin",
            modifier = Modifier.size(coinSize)
        )
        Spacer(modifier = Modifier.width(spacing))
        Text(
            text = price.toString(),
            fontFamily = JuaFontFamily,
            fontSize = fontSize,
            color = textColor
        )
    }
}

/**
 * The 21 Plants shop slots (7x3 grid), in Figma/attachment order. `null` marks a slot whose
 * artwork hasn't been provided yet - only 16 of the 21 plant images have arrived so far, so the
 * last 5 render as empty placeholder cards until the rest come in.
 */
private val mysteryBagShopItems: List<Pair<Int, String>> = listOf(
    R.drawable.bag_flower to "Flower Bag",
    R.drawable.bag_vegetable to "Vegetable Bag",
    R.drawable.bag_fruit to "Fruit Bag",
    R.drawable.bag_decoration to "Decoration Bag"
)

private val plantShopItems: List<Pair<Int, String>> = listOf(
    // Row 1: Sunflower, Tulip, Lavender, Rose, Marigold, Hibiscus, Daisy
    R.drawable.plant_sunflower to "Sunflower",
    R.drawable.plant_tulip to "Tulip",
    R.drawable.plant_lavender to "Lavender",
    R.drawable.plant_rose to "Rose",
    R.drawable.plant_marigold to "Marigold",
    R.drawable.plant_hibiscus to "Hibiscus",
    R.drawable.plant_daisy to "Daisy",
    // Row 2: Pumpkin, Tomato, Apple, Strawberry, Watermelon, Orange, Banana
    R.drawable.plant_pumpkin to "Pumpkin",
    R.drawable.plant_tomato to "Tomato",
    R.drawable.plant_apple to "Apple",
    R.drawable.plant_strawberry to "Strawberry",
    R.drawable.plant_watermelon to "Watermelon",
    R.drawable.plant_orange to "Orange",
    R.drawable.plant_banana to "Banana",
    // Row 3: Corn, Broccoli, Lettuce, Mushroom, Carrot, Blueberry, Grape
    R.drawable.plant_corn to "Corn",
    R.drawable.plant_broccoli to "Broccoli",
    R.drawable.plant_lettuce to "Lettuce",
    R.drawable.plant_mushroom to "Mushroom",
    R.drawable.plant_carrot to "Carrot",
    R.drawable.plant_blueberry to "Blueberry",
    R.drawable.plant_grape to "Grape"
)

/**
 * The Decorations shop's 21 grid slots (same 7x3 layout as Plants), matching the real
 * "Shop Decorations" Figma frame. Only 17 of the 21 items have arrived so far - the last
 * row's final 4 columns are still `null` placeholders until that artwork comes in.
 */
private val decorationShopItems: List<Pair<Int, String>?> = listOf(
    // Row 1: Fence, Scarecrow, Fountain, Rose Arch, Watering Can, Square Pot, Shovel
    R.drawable.decor_fence to "Fence",
    R.drawable.decor_scarecrow to "Scarecrow",
    R.drawable.decor_fountain to "Fountain",
    R.drawable.decor_rose_arch to "Rose Arch",
    R.drawable.decor_watering_can to "Watering Can",
    R.drawable.decor_square_pot to "Square Pot",
    R.drawable.decor_shovel to "Shovel",
    // Row 2: Bench, Rocks, Paved Path, Round Pot, Gnome, Stone Path, Bird Bath
    R.drawable.decor_bench to "Bench",
    R.drawable.decor_rocks to "Rocks",
    R.drawable.decor_paved_path to "Paved Path",
    R.drawable.decor_round_pot to "Round Pot",
    R.drawable.decor_gnome to "Gnome",
    R.drawable.decor_stone_path to "Stone Path",
    R.drawable.decor_bird_bath to "Bird Bath",
    // Row 3: Wishing Well, Light, Picnic Table, then 4 not-yet-provided slots
    R.drawable.decor_wishing_well to "Wishing Well",
    R.drawable.decor_light to "Light",
    R.drawable.decor_picnic_table to "Picnic Table",
    null, null, null, null
)

/**
 * One entry in a notification list: the sender/action text plus which icon to show, plus the
 * garden name + username that VISIT should open (SillyDog283 matches their real "Little
 * Garden" entry in the Visit Gardens list; the rest are made-up garden names for senders who
 * don't otherwise appear in this app, same spirit as the made-up Journal prompts).
 */
private data class NotificationItem(
    val iconRes: Int,
    val text: String,
    val gardenName: String,
    val username: String
)

/**
 * Matches the real Figma "Notifications" frame: 2 unread items shown as-is, plus 4 older ones
 * (order taken from the design's own top-to-bottom stacking within each section).
 */
private val newNotifications = listOf(
    NotificationItem(R.drawable.notif_heart, "SillyDog283 sent a heart to your garden!", "Little Garden", "SillyDog283"),
    NotificationItem(R.drawable.notif_tulip, "MerryMarina sent a Tulip to your garden!", "Marina's Meadow", "MerryMarina")
)

private val pastNotifications = listOf(
    NotificationItem(R.drawable.notif_flower, "DragonRider23 sent a flower to your garden!", "Dragon's Den", "DragonRider23"),
    NotificationItem(R.drawable.notif_smiley, "Mocha_Lover823 sent a smiley to your garden!", "Mocha Grove", "Mocha_Lover823"),
    NotificationItem(R.drawable.notif_smiley, "MysteriousMoo444 sent a Smiley to your garden!", "Moonlit Meadow", "MysteriousMoo444"),
    NotificationItem(R.drawable.notif_star, "RacoonDog817 sent a star to your garden!", "Raccoon Ridge", "RacoonDog817")
)

/**
 * "Notifications" screen - matches the real Figma "Notifications" frame. Full-screen like Shop,
 * with a "New Notifications" column (fixed, doesn't scroll) and a "Past Notifications" column
 * (scrollable - the CSS gives only that side `overflow-y: scroll`) side by side, divided by a
 * vertical line, matching the design's own two labeled sections.
 */
@Composable
private fun NotificationsScreen(onClose: () -> Unit, onVisit: (NotificationItem) -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(CardCream)) {
    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        // Header: "NOTIFICATION" title, close X
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "NOTIFICATION",
                fontFamily = JuaFontFamily,
                fontSize = 26.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "✕",
                fontFamily = JuaFontFamily,
                fontSize = 22.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.CenterEnd).clickable(onClick = onClose)
            )
        }

        Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 28.dp, vertical = 8.dp)) {
            // New Notifications (left column) - now scrollable so extra items can be revealed
            // by scrolling instead of needing to fit on screen.
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    text = "New Notifications",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    newNotifications.forEach { NotificationCard(it, onVisit) }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .width(1.dp)
                    .background(Color.Black)
            )

            // Past Notifications (right column) - scrollable, matches CSS's overflow-y: scroll.
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    text = "Past Notifications",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    lazyColumnItems(pastNotifications) { NotificationCard(it, onVisit) }
                }
            }
        }
    }
    }
}

/**
 * One notification row: icon, sender/action text, and a "VISIT" button - matches the real
 * Figma card (peach background, drop shadow, red VISIT pill) scaled down to fit a half-width
 * column instead of the design's near-full-width card.
 */
@Composable
private fun NotificationCard(item: NotificationItem, onVisit: (NotificationItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SelectedPeach)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(item.iconRes),
            contentDescription = null,
            modifier = Modifier.size(58.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.text,
            fontFamily = JuaFontFamily,
            fontSize = 15.sp,
            lineHeight = 18.sp,
            color = TextBlack,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(ButtonRed)
                .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                .clickable { onVisit(item) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "VISIT",
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = ButtonTextCream
            )
        }
    }
}

/**
 * One garden entry: circular avatar, garden name, owner username, and a "VISIT" button -
 * matches the real Figma "Visit Gardens" card layout.
 */
private data class VisitGardenItem(val avatarRes: Int, val gardenName: String, val username: String)

private val thisWeeksShowcase = listOf(
    VisitGardenItem(R.drawable.avatar_sprout, "My Sprout Land", "Sunny_Sprout"),
    VisitGardenItem(R.drawable.avatar_little_garden, "Little Garden", "SillyDog283")
)

private val otherGardens = listOf(
    VisitGardenItem(R.drawable.avatar_fairy_land, "Fairy Land", "Ghastly_Tim21"),
    VisitGardenItem(R.drawable.avatar_cozy_cove, "Cozy Cove", "SuperFly42"),
    VisitGardenItem(R.drawable.avatar_home_garden, "Home Garden", "Faith91"),
    VisitGardenItem(R.drawable.avatar_safe_space, "Safe Space", "RobDog25"),
    VisitGardenItem(R.drawable.avatar_my_little_land, "My Little Land", "SunnyMe"),
    VisitGardenItem(R.drawable.avatar_plant_city, "Plant City", "BookWorm91")
)

/**
 * "Visit Gardens" screen - matches the real Figma "Visit Gardens" frame. Full-screen like
 * Notifications, with a This Week's Showcase column (fixed) and an Other Gardens column
 * (scrollable) side by side, divided by a vertical line.
 */
@Composable
private fun VisitGardensScreen(onClose: () -> Unit, onVisit: (VisitGardenItem) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardCream)
    ) {
    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "VISIT GARDENS",
                fontFamily = JuaFontFamily,
                fontSize = 26.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "✕",
                fontFamily = JuaFontFamily,
                fontSize = 22.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.CenterEnd).clickable(onClick = onClose)
            )
        }

        Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 28.dp, vertical = 8.dp)) {
            // This Week's Showcase (left column) - now scrollable so extra items can be
            // revealed by scrolling instead of needing to fit on screen.
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    text = "This Week's Showcase",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    thisWeeksShowcase.forEach { GardenCard(it, onVisit) }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .width(1.dp)
                    .background(Color.Black)
            )

            // Other Gardens (right column) - scrollable, matches CSS's overflow-y: scroll.
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    text = "Other Gardens",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    lazyColumnItems(otherGardens) { GardenCard(it, onVisit) }
                }
            }
        }
    }
    }
}

/**
 * One garden row: circular avatar, garden name + username, and a "VISIT" button - matches the
 * real Figma card (peach background, red VISIT pill) scaled down to fit a half-width column
 * instead of the design's near-full-width card.
 */
@Composable
private fun GardenCard(item: VisitGardenItem, onVisit: (VisitGardenItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SelectedPeach)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bigger circular frame with its own background so a face isn't cropped small/high -
        // the avatar image is centered and scaled to fill the frame rather than tightly cropped.
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(CardCream),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(item.avatarRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.gardenName,
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = TextBlack
            )
            Text(
                text = item.username,
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = Color.Black.copy(alpha = 0.4f)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(ButtonRed)
                .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                .clickable { onVisit(item) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "VISIT",
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = ButtonTextCream
            )
        }
    }
}

/** A quest's completion state - drives its card's background/border and its status control. */
private enum class QuestStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }

/**
 * One quest entry: icon, task description, optional progress line (e.g. "38%"), and status -
 * matches the real Figma "Quests" card layout.
 */
private data class QuestItem(
    val iconRes: Int,
    val text: String,
    val status: QuestStatus,
    val progressText: String? = null
)

private val weatherSpecialQuests = listOf(
    QuestItem(R.drawable.icon_weather, "Draw the weather in your journal", QuestStatus.NOT_STARTED),
    QuestItem(R.drawable.quest_book, "Journal your favorite memory", QuestStatus.NOT_STARTED)
)

private val dailyQuests = listOf(
    QuestItem(R.drawable.quest_walk, "Walk for 10 minutes", QuestStatus.COMPLETED, "100%"),
    QuestItem(R.drawable.quest_walk, "Walk for 1 mile", QuestStatus.IN_PROGRESS, "38%"),
    QuestItem(R.drawable.notif_smiley, "Send a heart to another virtual garden", QuestStatus.NOT_STARTED),
    QuestItem(R.drawable.quest_quote, "Read the inspirational quote of the day", QuestStatus.NOT_STARTED),
    QuestItem(R.drawable.quest_book, "Journal your thoughts and feelings", QuestStatus.NOT_STARTED)
)

/**
 * "Quests" screen - matches the real Figma "Quests" frame. Full-screen like Notifications/
 * Visit Gardens, with a Weather Special Quests column (fixed) and a Daily Quests column
 * (scrollable) side by side, divided by a vertical line. The Figma title uses a bigger
 * 32sp treatment than the other screens' 26sp headers, so that's kept as given.
 */
@Composable
private fun QuestsScreen(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CardCream)
    ) {
    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "QUESTS",
                fontFamily = JuaFontFamily,
                fontSize = 32.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = "✕",
                fontFamily = JuaFontFamily,
                fontSize = 22.sp,
                color = TextBlack,
                modifier = Modifier.align(Alignment.CenterEnd).clickable(onClick = onClose)
            )
        }

        Row(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 28.dp, vertical = 8.dp)) {
            // Weather Special Quests (left column) - now scrollable so extra items can be
            // revealed by scrolling instead of needing to fit on screen.
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    text = "Weather Special Quests",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    weatherSpecialQuests.forEach { QuestCard(it) }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .width(1.dp)
                    .background(Color.Black)
            )

            // Daily Quests (right column) - scrollable, matches CSS's overflow-y: scroll.
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Text(
                    text = "Daily Quests",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    lazyColumnItems(dailyQuests) { QuestCard(it) }
                }
            }
        }
    }
    }
}

/**
 * One quest row: icon, task text (+ optional progress line), and a status control on the
 * right - a red "START" pill for not-yet-started quests, plain colored text for in-progress
 * ("IN PROGRESS", red) and completed ("COMPLETE", green) ones. The card itself is peach with
 * no border for active quests, matching the CSS's completed-quest card which drops the peach
 * fill for a plain black-bordered outline instead.
 */
@Composable
private fun QuestCard(item: QuestItem) {
    val isCompleted = item.status == QuestStatus.COMPLETED
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isCompleted) CardCream else SelectedPeach)
            .then(
                if (isCompleted) {
                    Modifier.border(1.dp, Color.Black, RoundedCornerShape(14.dp))
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(item.iconRes),
            contentDescription = null,
            modifier = Modifier.size(58.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.text,
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                color = TextBlack
            )
            if (item.progressText != null) {
                Text(
                    text = item.progressText,
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = TextBlack
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        when (item.status) {
            QuestStatus.NOT_STARTED -> {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ButtonRed)
                        .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "START",
                        fontFamily = JuaFontFamily,
                        fontSize = 15.sp,
                        color = ButtonTextCream
                    )
                }
            }
            QuestStatus.IN_PROGRESS -> {
                Text(
                    text = "IN PROGRESS",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = ButtonRed
                )
            }
            QuestStatus.COMPLETED -> {
                Text(
                    text = "COMPLETE",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = Color(0xFF69BA76)
                )
            }
        }
    }
}

/** Which screen a garden visit was launched from, so the Leave button can return there. */
/** One selectable option on the Add Decoration / Add Plant screens, with how many are owned. */
private data class AddItemOption(val iconRes: Int, val label: String, val owned: Int)

// Left-to-right order matches the real Figma "Add Decoration" frame.
private val decorationOptions = listOf(
    AddItemOption(R.drawable.decor_fence, "Fence", 2),
    AddItemOption(R.drawable.decor_watering_can, "Watering Can", 1),
    AddItemOption(R.drawable.decor_shovel, "Shovel", 1),
    AddItemOption(R.drawable.decor_paved_path, "Path", 5)
)

// Left-to-right order matches the real Figma "Add Plant" frame.
private val plantOptions = listOf(
    AddItemOption(R.drawable.plant_sunflower, "Sunflower", 1),
    AddItemOption(R.drawable.plant_tulip, "Tulip", 1),
    AddItemOption(R.drawable.plant_daisy, "Daisy", 1),
    AddItemOption(R.drawable.plant_carrot, "Carrot", 2)
)

/**
 * Shared implementation of the "Add Decoration" and "Add Plant" screens - matches the real
 * Figma frames, which are identical apart from their title, item list, confirm-button label,
 * and the owned-count badge's border color (`#73593B` NoteBrown for decorations, `#9A9A9A`
 * PriceBorderGray for plants). Tap an item to select it, then the confirm button (only enabled
 * once something is selected) closes back to the Edit Garden screen underneath - there's no
 * real garden-placement/inventory system behind this yet, so it's a placeholder confirm action
 * for now, same spirit as this app's other confirm buttons.
 */
@Composable
private fun AddItemScreen(
    title: String,
    items: List<AddItemOption>,
    confirmLabel: String,
    badgeBorderColor: Color,
    onClose: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(SelectedPeach)) {
        Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header: title left, X close right - same header padding as Shop's.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = title,
                        fontFamily = JuaFontFamily,
                        fontSize = 20.sp,
                        color = TextBlack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "✕",
                        fontFamily = JuaFontFamily,
                        fontSize = 22.sp,
                        color = TextBlack,
                        modifier = Modifier.align(Alignment.CenterEnd).clickable(onClick = onClose)
                    )
                }

                // Item grid - same 7-column grid, spacing, side padding, and card size
                // (106x140) as the Shop screen's grids, so this lines up identically; it just
                // starts a handful of items from the left instead of filling all 7 columns.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 40.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp)
                ) {
                    itemsIndexed(items) { index, item ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            AddItemCard(
                                item = item,
                                isSelected = selectedIndex == index,
                                badgeBorderColor = badgeBorderColor,
                                onClick = { selectedIndex = index }
                            )
                        }
                    }
                }
            }

            // Confirm button - bottom-right, per the real Figma frame.
            val canConfirm = selectedIndex != null
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .shadow(4.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (canConfirm) ButtonRed else PriceBorderGray)
                    .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                    .then(if (canConfirm) Modifier.clickable(onClick = onClose) else Modifier)
                    .padding(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Text(
                    text = confirmLabel,
                    fontFamily = JuaFontFamily,
                    fontSize = 20.sp,
                    color = ButtonTextCream,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * One item card on the Add Decoration / Add Plant screens: name, image, and an owned-count
 * badge - same 106x140 card size as the Shop screen's `MysteryBagCard` (just its own layout,
 * since the badge here is an owned-count "xN", not a coin price). Tapping it selects it
 * (cream -> peach with a red border), matching this app's usual selected-state treatment
 * elsewhere since the real CSS is a static mock with no selected state shown.
 */
@Composable
private fun AddItemCard(
    item: AddItemOption,
    isSelected: Boolean,
    badgeBorderColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(106.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SelectedPeach else CardCream)
            .border(
                if (isSelected) 2.dp else 1.dp,
                if (isSelected) ButtonRed else FieldBorderBlack,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.label,
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = TextBlack,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Image(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            modifier = Modifier.size(68.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(CardCream)
                .border(1.dp, badgeBorderColor, RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "\u00d7" + item.owned,
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = TextBlack
            )
        }
    }
}

private enum class VisitOrigin { NOTIFICATIONS, VISIT_GARDENS }

/** The garden + owner currently being visited, and which screen the visit started from. */
private data class VisitTarget(val gardenName: String, val username: String, val origin: VisitOrigin)

/** One sticker option on the Send a Message screen. */
private data class MessageSticker(val iconRes: Int, val label: String)

// Ordered to match the real Figma grid: row 1 is Thank You / Froggy / Bear Hug, row 2 is
// Tulip / Wink / Star.
private val messageStickers = listOf(
    MessageSticker(R.drawable.sticker_thank_you, "Thank You"),
    MessageSticker(R.drawable.sticker_frog, "Froggy"),
    MessageSticker(R.drawable.sticker_bear_heart, "Bear Hug"),
    MessageSticker(R.drawable.sticker_tulip, "Tulip"),
    MessageSticker(R.drawable.sticker_smiley_wink, "Wink"),
    MessageSticker(R.drawable.sticker_star, "Star")
)

/**
 * "Visiting a Garden" screen - shown on top of Notifications/Visit Gardens after tapping a
 * VISIT button; the screen it was launched from stays open underneath (its `show*Screen` flag
 * is never turned off), so tapping Leave just clears `visitTarget` and reveals it again -
 * whichever one that was. Matches the real Figma "Main Visiting" frame.
 */
@Composable
private fun VisitingGardenScreen(target: VisitTarget, onLeave: () -> Unit) {
    // Matches the real Figma "Main Visiting" / "Send Message" frames, which turn out to be
    // the same screen in two states: the "Send a Message" tab at bottom-right is a short pill
    // that expands upward into a taller panel (header + sticker grid + SEND button) when
    // tapped, while the background, "You are visiting" card, and LEAVE button stay in place.
    var isSendPanelExpanded by remember { mutableStateOf(false) }
    var selectedSticker by remember { mutableStateOf<Int?>(null) }
    var showSentBanner by remember { mutableStateOf(false) }

    LaunchedEffect(showSentBanner) {
        if (showSentBanner) {
            delay(2200)
            showSentBanner = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(CardCream)) {
        Image(
            painter = painterResource(R.drawable.visiting_garden_background),
            contentDescription = "Garden background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            // "You are visiting: [Garden]" card - top-left, per the real Figma frame.
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
                    .widthIn(max = 260.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardCream)
                    .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "You are visiting: " + target.gardenName,
                    fontFamily = JuaFontFamily,
                    fontSize = 20.sp,
                    color = NoteBrown
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = target.username,
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown
                )
            }

            // "LEAVE" button - bottom-left, per the real Figma frame. Returns to whichever
            // screen launched this visit (that screen was never closed, just covered).
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
                    .shadow(4.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(ButtonRed)
                    .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                    .clickable(onClick = onLeave)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "LEAVE",
                    fontFamily = JuaFontFamily,
                    fontSize = 20.sp,
                    color = ButtonTextCream,
                    textAlign = TextAlign.Center
                )
            }

            // "Send a Message" tab/panel - bottom-right. Both CSS frames place it at almost
            // exactly the same right edge and bottom edge, just with the top edge moving up
            // when expanded - so it's implemented as one card that grows upward in place.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .widthIn(max = 210.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardCream)
                    .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
            ) {
                AnimatedVisibility(
                    visible = isSendPanelExpanded,
                    enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Row 1: Thank You, Froggy, Bear Hug. Row 2: Tulip, Wink, Star -
                        // matches the two rows of sticker slots in the real Figma frame.
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StickerTile(messageStickers[0], selectedSticker) { selectedSticker = it }
                            StickerTile(messageStickers[1], selectedSticker) { selectedSticker = it }
                            StickerTile(messageStickers[2], selectedSticker) { selectedSticker = it }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StickerTile(messageStickers[3], selectedSticker) { selectedSticker = it }
                            StickerTile(messageStickers[4], selectedSticker) { selectedSticker = it }
                            StickerTile(messageStickers[5], selectedSticker) { selectedSticker = it }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            val canSend = selectedSticker != null
                            Box(
                                modifier = Modifier
                                    .shadow(4.dp, RoundedCornerShape(10.dp))
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (canSend) ButtonRed else PriceBorderGray)
                                    .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                                    .then(if (canSend) Modifier.clickable { showSentBanner = true } else Modifier)
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "SEND",
                                    fontFamily = JuaFontFamily,
                                    fontSize = 16.sp,
                                    color = ButtonTextCream,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isSendPanelExpanded = !isSendPanelExpanded }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Send a Message",
                        fontFamily = JuaFontFamily,
                        fontSize = 15.sp,
                        color = TextBlack
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Real design flips this arrow to point up when collapsed, and lets it
                    // point down (its natural direction) once the panel is expanded.
                    Text(
                        text = if (isSendPanelExpanded) "▼" else "▲",
                        fontFamily = JuaFontFamily,
                        fontSize = 15.sp,
                        color = TextBlack
                    )
                }
            }
        }

        // "Message sent!" banner - not present in any design, added because it was explicitly
        // requested; pops in top-right and auto-dismisses.
        AnimatedVisibility(
            visible = showSentBanner,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 8.dp, end = 16.dp),
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(LogoGreen)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Message sent successfully!",
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = ButtonTextCream
                )
            }
        }
    }
}

/**
 * One sticker tile in the Send a Message panel - cream with a raised shadow when unselected,
 * peach (`#FACFA4`, matching the real CSS) and flat/pressed-looking when selected. Only one
 * sticker can be selected at a time.
 */
@Composable
private fun StickerTile(sticker: MessageSticker, selectedIconRes: Int?, onSelect: (Int) -> Unit) {
    val isSelected = selectedIconRes == sticker.iconRes
    Box(
        modifier = Modifier
            .size(52.dp)
            .then(if (!isSelected) Modifier.shadow(3.dp, RoundedCornerShape(10.dp)) else Modifier)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) SelectedPeach else CardCream)
            .clickable { onSelect(sticker.iconRes) }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(sticker.iconRes),
            contentDescription = sticker.label,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * One recent journal entry: title, timestamp caption, page number, and an optional preview
 * snippet for the list card - plus the fuller data (`prompt`/`fullBody`/`photoRes`) used by
 * the entry viewer screen when you tap into it. `preview` is null for entries whose preview
 * text the CSS marks `visibility: hidden` (art entries have no text preview at all).
 * `viewerReady` is false for entries whose full viewer design hasn't been specced yet - they
 * still render in the list, just without a working tap-through until that CSS arrives.
 */
private data class JournalEntry(
    val title: String,
    val caption: String,
    val pageNumber: Int,
    val preview: String?,
    val type: EntryEditorMode,
    val date: String,
    val prompt: String,
    val fullBody: String,
    val photoRes: Int? = null,
    val artRes: Int? = null,
    val viewerReady: Boolean = true
)

private val journalEntries = listOf(
    JournalEntry(
        title = "Fox!",
        caption = "Yesterday, art",
        pageNumber = 4,
        preview = null,
        type = EntryEditorMode.DRAWING,
        date = "10/3/25",
        prompt = "Today's Prompt: Draw your favorite animal!",
        fullBody = "",
        artRes = R.drawable.entry_drawing_fox
    ),
    JournalEntry(
        title = "My Parents",
        caption = "Yesterday, writing",
        pageNumber = 3,
        preview = "I'm really grateful for...",
        type = EntryEditorMode.WRITING,
        date = "10/3/25",
        prompt = "Today's Prompt: What are you most grateful for?",
        fullBody = "I'm really grateful for my parents today. They've always worked so hard to " +
            "give me opportunities, even when they never had the same ones themselves. I " +
            "sometimes take that for granted, but lately I've been noticing all the quiet " +
            "things they do — like leaving food for me when they know I'm studying late, or " +
            "pretending not to worry even when I can tell they're exhausted. It makes me " +
            "realize how much love can be shown in small, unspoken ways. I'm also thankful " +
            "for how our relationship has grown. We still argue sometimes, but I think we " +
            "understand each other more now than before. I'm learning to see them not just " +
            "as parents, but as people with their own worries and dreams. That thought alone " +
            "makes me want to try harder — not because they expect it, but because I want to " +
            "make them proud.",
        photoRes = R.drawable.entry_photo_my_parents
    ),
    JournalEntry(
        title = "Anger",
        caption = "Yesterday, writing",
        pageNumber = 2,
        preview = "I feel frustrated to...",
        type = EntryEditorMode.WRITING,
        date = "10/1/25",
        prompt = "Today's Prompt: What made you feel frustrated today?",
        fullBody = "I feel frustrated to...",
        viewerReady = false
    ),
    JournalEntry(
        title = "First Journal",
        caption = "Yesterday, writing",
        pageNumber = 1,
        preview = "I've never used this....",
        type = EntryEditorMode.WRITING,
        date = "9/28/25",
        prompt = "Today's Prompt: Write about how you're feeling right now.",
        fullBody = "I've never used this....",
        viewerReady = false
    )
)

/**
 * "Journal" screen - matches the real Figma "My Journal" frame. Unlike the other full-screen
 * pages, this one is authored upright (no page-level rotate), so its layout maps directly: a
 * peach page background, a centered cream "MY JOURNAL" title pill, a fixed (non-scrolling)
 * stack of recent-entry cards, and a floating red "CREATE NEW" button pinned bottom-right.
 */
@Composable
private fun JournalScreen(onClose: () -> Unit) {
    // The rest of the app is locked to landscape (see AndroidManifest), but this one screen
    // is authored upright in Figma. Force portrait for as long as this screen is on-screen,
    // then restore landscape when it closes - the user rotates their phone to use it.
    val activity = LocalContext.current as? Activity
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    var showCreateNewEntryDialog by remember { mutableStateOf(false) }
    var entryEditorMode by remember { mutableStateOf<EntryEditorMode?>(null) }
    var viewingEntryIndex by remember { mutableStateOf<Int?>(null) }

    val currentEditorMode = entryEditorMode
    if (currentEditorMode != null) {
        JournalEntryEditorScreen(
            mode = currentEditorMode,
            onClose = { entryEditorMode = null }
        )
        return
    }

    val currentViewingIndex = viewingEntryIndex
    if (currentViewingIndex != null) {
        JournalEntryViewerScreen(
            entries = journalEntries,
            initialIndex = currentViewingIndex,
            onClose = { viewingEntryIndex = null }
        )
        return
    }

    // Outer box paints edge-to-edge (the app draws behind system bars/the camera cutout via
    // enableEdgeToEdge), so the peach background still fills the whole screen including under
    // the status bar and cutout.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SelectedPeach)
    ) {
        // Inner box insets its CONTENT by the real safe-drawing area (status bar, the Pixel
        // 8 Pro's camera cutout, and nav bar/gesture area) so nothing sits under them. This
        // is insets-driven rather than a hardcoded offset, so it stays correct regardless of
        // which edge the cutout ends up on if the device is ever rotated.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardCream)
                    .padding(horizontal = 40.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "MY JOURNAL",
                    fontFamily = JuaFontFamily,
                    fontSize = 32.sp,
                    color = TextBlack
                )
            }

            Text(
                text = "✕",
                fontFamily = JuaFontFamily,
                fontSize = 28.sp,
                color = TextBlack,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 20.dp)
                    .clickable(onClick = onClose)
            )

            // Recent entries - fixed stack, matches the CSS's 4 statically-placed cards (no
            // overflow-y in the source, unlike the two-column screens' scrollable side).
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                journalEntries.forEachIndexed { index, entry ->
                    JournalEntryCard(
                        entry = entry,
                        onClick = { if (entry.viewerReady) viewingEntryIndex = index }
                    )
                }
            }

            // Floating "CREATE NEW" button, bottom-right - matches the CSS's red pill +
            // plus-circle icon near the bottom-right corner.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ButtonRed)
                    .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                    .clickable { showCreateNewEntryDialog = true }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(CardCream)
                            .border(2.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "+", fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CREATE NEW",
                        fontFamily = JuaFontFamily,
                        fontSize = 20.sp,
                        color = ButtonTextCream
                    )
                }
            }
        }

        // "Create New Entry" modal - matches the real Figma frame of the same name: a dark
        // scrim over the Journal screen with a centered popup offering "Drawing" or "Writing"
        // as the new entry type. Covers the true full screen (not the inset content box) so
        // the scrim reaches every edge, same as the CSS's full-frame Rectangle 31.
        if (showCreateNewEntryDialog) {
            CreateNewEntryDialog(
                onDismiss = { showCreateNewEntryDialog = false },
                onSelectMode = { mode ->
                    entryEditorMode = mode
                    showCreateNewEntryDialog = false
                }
            )
        }
    }
}

/**
 * One journal entry card: title + page label on top, timestamp caption + body preview
 * underneath - matches the real Figma entry card (cream card on the peach page, drop shadow).
 */
@Composable
private fun JournalEntryCard(entry: JournalEntry, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardCream)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = entry.title,
                fontFamily = JuaFontFamily,
                fontSize = 16.sp,
                color = TextBlack
            )
            Text(
                text = "pg ${entry.pageNumber}",
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = NoteBrown
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = entry.caption,
                fontFamily = JuaFontFamily,
                fontSize = 15.sp,
                color = NoteBrown,
                modifier = Modifier.weight(0.4f)
            )
            if (entry.preview != null) {
                Text(
                    text = entry.preview,
                    fontFamily = JuaFontFamily,
                    fontSize = 15.sp,
                    color = NoteBrown,
                    modifier = Modifier.weight(0.6f)
                )
            }
        }
    }
}

/**
 * "Create New Entry" modal - matches the real Figma frame: a dark scrim over the Journal
 * screen with a centered cream popup offering two entry types, "Drawing" and "Writing".
 * Neither option is wired to a destination yet since only this choice screen was specced.
 */
@Composable
private fun CreateNewEntryDialog(onDismiss: () -> Unit, onSelectMode: (EntryEditorMode) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(240.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CardCream)
                .clickable(enabled = false) {}
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create New...",
                fontFamily = JuaFontFamily,
                fontSize = 24.sp,
                color = TextBlack,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            // No dedicated "ruler & pen" / "text" icon assets exist yet, so these reuse the
            // closest icons already in the app (the pencil "edit" icon for Drawing, the book
            // "journal" icon for Writing) as stand-ins.
            CreateEntryOption(
                iconRes = R.drawable.icon_edit,
                label = "Drawing",
                onClick = { onSelectMode(EntryEditorMode.DRAWING) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            CreateEntryOption(
                iconRes = R.drawable.icon_journal,
                label = "Writing",
                onClick = { onSelectMode(EntryEditorMode.WRITING) }
            )
        }
    }
}

@Composable
private fun CreateEntryOption(iconRes: Int, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SelectedPeach)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontFamily = JuaFontFamily,
            fontSize = 20.sp,
            color = TextBlack
        )
    }
}

/** Which kind of new journal entry is being created - drives the editor screen's content. */
private enum class EntryEditorMode { DRAWING, WRITING }

/**
 * Shared "new entry" editor screen for both the real Figma "Drawing" and "Writing" frames -
 * they're nearly identical (same "MY JOURNAL" header, date/type label, title/prompt, and a
 * floating toolbar pill) and differ only in their sample title/prompt copy, the type label,
 * and which tools sit in the bottom toolbar. Reached from the "Create New Entry" modal, and
 * stays under the same portrait lock as the rest of the Journal flow since it's just a state
 * swap inside JournalScreen rather than a separate top-level screen.
 *
 * The Writing body is a real editable text field; Drawing's canvas is a visual placeholder
 * for now (no drawing engine wired up yet), and none of the toolbar tools (Pencil/Eraser/
 * Color/Undo/Redo/Photo) are functional - same visual-only fidelity as the rest of the app's
 * decorative buttons (VISIT, START, etc.).
 */
@Composable
private fun JournalEntryEditorScreen(mode: EntryEditorMode, onClose: () -> Unit) {
    val title = if (mode == EntryEditorMode.DRAWING) "Today's Weather!" else "Getting My Dog"
    val prompt = if (mode == EntryEditorMode.DRAWING) {
        "Today's Prompt: Draw Today's Weather"
    } else {
        "Today's Prompt: Write about your favorite memory"
    }
    val typeLabel = if (mode == EntryEditorMode.DRAWING) "Art" else "Writing"
    val dateLabel = remember { SimpleDateFormat("M/d/yy", Locale.getDefault()).format(Date()) }
    var bodyText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SelectedPeach)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardCream)
                        .padding(horizontal = 40.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "MY JOURNAL",
                        fontFamily = JuaFontFamily,
                        fontSize = 32.sp,
                        color = TextBlack
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Trash (discard) and Save both just return to the entry list for now - there's
                // no persistence layer yet to actually write the entry anywhere. Sits in its
                // own row below the title pill, not beside it.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_delete),
                        contentDescription = "Discard",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(onClick = onClose)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ButtonRed)
                            .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                            .clickable(onClick = onClose)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "SAVE",
                            fontFamily = JuaFontFamily,
                            fontSize = 20.sp,
                            color = ButtonTextCream
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The white "page" - matches the CSS's full-bleed white card. The floating
                // toolbar pill sits on top of its bottom edge, same overlap as the source.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(horizontal = 20.dp)
                            .padding(top = 16.dp, bottom = 90.dp)
                    ) {
                        Text(text = dateLabel, fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)
                        Text(text = typeLabel, fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = title,
                            fontFamily = JuaFontFamily,
                            fontSize = 20.sp,
                            color = TextBlack,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = prompt,
                            fontFamily = JuaFontFamily,
                            fontSize = 15.sp,
                            color = PriceBorderGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (mode == EntryEditorMode.WRITING) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                if (bodyText.isEmpty()) {
                                    Text(
                                        text = "Start writing...",
                                        fontFamily = JuaFontFamily,
                                        fontSize = 15.sp,
                                        color = PriceBorderGray
                                    )
                                }
                                BasicTextField(
                                    value = bodyText,
                                    onValueChange = { bodyText = it },
                                    textStyle = TextStyle(
                                        fontFamily = JuaFontFamily,
                                        fontSize = 15.sp,
                                        color = TextBlack
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            // No drawing engine yet - a blank canvas placeholder, matching the
                            // CSS which likewise has no drawn content in this area.
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }

                    // Floating toolbar pill - Pencil/Eraser/Color/Undo/Redo for Drawing,
                    // Photo/Undo/Redo for Writing, matching each CSS's own icon set and order.
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardCream)
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (mode == EntryEditorMode.DRAWING) {
                            EditorTool(label = "Pencil") {
                                Image(
                                    painter = painterResource(R.drawable.icon_edit),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            EditorTool(label = "Eraser") {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF6C9D6))
                                        .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                                )
                            }
                            EditorTool(label = "Color") {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6EBAED))
                                )
                            }
                            EditorTool {
                                Text(text = "UNDO", fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)
                            }
                            EditorTool {
                                Text(text = "REDO", fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)
                            }
                        } else {
                            EditorTool(label = "Photo") {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CardCream)
                                        .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                                )
                            }
                            EditorTool {
                                Text(text = "UNDO", fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)
                            }
                            EditorTool {
                                Text(text = "REDO", fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** One bottom-toolbar tool: an icon slot on top, with an optional label underneath. */
@Composable
private fun EditorTool(label: String? = null, icon: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        icon()
        if (label != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)
        }
    }
}

/**
 * Read-only viewer for a saved journal entry - matches the real Figma "Writing Entry" and
 * "Drawing Entry" frames, which share the same header/back-arrow/white-card/page-number/
 * chevron-nav shell and differ only in what fills the card body (a text passage for Writing,
 * artwork for Drawing). The bottom chevrons page through every entry in `entries`, not just
 * the ones with full CSS-specified content - entries without their own viewer design yet
 * (`viewerReady = false`) still show up here with their best-available data (preview text
 * standing in for a full body, no prompt line) rather than dead-ending the navigation.
 */
@Composable
private fun JournalEntryViewerScreen(
    entries: List<JournalEntry>,
    initialIndex: Int,
    onClose: () -> Unit
) {
    var index by remember { mutableStateOf(initialIndex) }
    val entry = entries[index]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SelectedPeach)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardCream)
                        .padding(horizontal = 40.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "MY JOURNAL",
                        fontFamily = JuaFontFamily,
                        fontSize = 32.sp,
                        color = TextBlack
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // "arrow_back" in the CSS - returns to the journal list (separate from the
                // bottom chevrons, which page between entries instead). Sits in its own row
                // below the title pill, not beside it.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_arrow_back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable(onClick = onClose)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The white "page" card, matching the CSS's Rectangle 29 - holds the date/type,
                // title/prompt, and the entry's own content (text or artwork).
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(horizontal = 20.dp)
                            .padding(top = 16.dp, bottom = 16.dp)
                    ) {
                        Text(text = entry.date, fontFamily = JuaFontFamily, fontSize = 15.sp, color = TextBlack)
                        Text(
                            text = if (entry.type == EntryEditorMode.DRAWING) "Art" else "Writing",
                            fontFamily = JuaFontFamily,
                            fontSize = 15.sp,
                            color = TextBlack
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = entry.title,
                            fontFamily = JuaFontFamily,
                            fontSize = 20.sp,
                            color = TextBlack,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (entry.prompt.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = entry.prompt,
                                fontFamily = JuaFontFamily,
                                fontSize = 15.sp,
                                color = PriceBorderGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        if (entry.type == EntryEditorMode.WRITING) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = entry.fullBody,
                                    fontFamily = JuaFontFamily,
                                    fontSize = 15.sp,
                                    lineHeight = 24.sp,
                                    color = TextBlack
                                )
                                if (entry.photoRes != null) {
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Image(
                                        painter = painterResource(entry.photoRes),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(314f / 209f)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        } else if (entry.artRes != null) {
                            Image(
                                painter = painterResource(entry.artRes),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        } else {
                            // No drawing engine or exported art asset for this entry yet - a
                            // blank canvas placeholder stands in for the CSS's illustration.
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }

                    Text(
                        text = entry.pageNumber.toString(),
                        fontFamily = JuaFontFamily,
                        fontSize = 15.sp,
                        color = TextBlack,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 6.dp, bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Prev/next chevrons - page through every entry, matching the CSS's red pill
                // "chevron_forward" buttons (one plain, one rotated 180deg for "back").
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val canGoBack = index > 0
                    val canGoForward = index < entries.size - 1
                    Box(
                        modifier = Modifier
                            .size(width = 39.dp, height = 37.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ButtonRed)
                            .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                            .clickable(enabled = canGoBack) { index-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "‹",
                            fontFamily = JuaFontFamily,
                            fontSize = 20.sp,
                            color = if (canGoBack) ButtonTextCream else ButtonTextCream.copy(alpha = 0.5f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 39.dp, height = 37.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ButtonRed)
                            .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
                            .clickable(enabled = canGoForward) { index++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "›",
                            fontFamily = JuaFontFamily,
                            fontSize = 20.sp,
                            color = if (canGoForward) ButtonTextCream else ButtonTextCream.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
