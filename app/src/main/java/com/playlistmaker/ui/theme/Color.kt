package com.playlistmaker.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val YpBlack = Color(0xFF1A1B22)
val YpBlue = Color(0xFF3772E7)
val YpLightBlue = Color(0xFF9FBBF3)
val YpLightGrey = Color(0xFFE6E8EB)
val YpTextGrey = Color(0xFFAEAFB4)
val YpGreen = Color(0xFF00D6C3)
val YpLightGreen = Color(0xFF76EAE0)
val YpOverlay = Color(0x801A1B22)

@Immutable
data class AppColors(
    val background: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val icon: Color,
    val searchField: Color,
    val searchFieldIcon: Color,
    val searchFieldText: Color,
    val searchFieldHint: Color,
    val inverseButton: Color,
    val inverseButtonText: Color,
    val bottomSheet: Color,
    val bottomSheetHandle: Color,
    val switchThumbChecked: Color,
    val switchTrackChecked: Color,
    val switchThumbUnchecked: Color,
    val switchTrackUnchecked: Color,
    val playerSecondaryButton: Color,
    val navigationSelected: Color,
    val navigationUnselected: Color,
    val accent: Color,
    val inactive: Color,
    val overlay: Color,
)

val LightAppColors = AppColors(
    background = White,
    textPrimary = YpBlack,
    textSecondary = YpTextGrey,
    icon = YpTextGrey,
    searchField = YpLightGrey,
    searchFieldIcon = YpTextGrey,
    searchFieldText = YpBlack,
    searchFieldHint = YpTextGrey,
    inverseButton = YpBlack,
    inverseButtonText = White,
    bottomSheet = White,
    bottomSheetHandle = YpLightGrey,
    switchThumbChecked = YpGreen,
    switchTrackChecked = YpLightGreen,
    switchThumbUnchecked = YpTextGrey,
    switchTrackUnchecked = YpLightGrey,
    playerSecondaryButton = Color(0x401A1B22),
    navigationSelected = YpBlue,
    navigationUnselected = YpBlack,
    accent = YpBlue,
    inactive = YpTextGrey,
    overlay = YpOverlay,
)

val DarkAppColors = AppColors(
    background = YpBlack,
    textPrimary = White,
    textSecondary = White,
    icon = White,
    searchField = White,
    searchFieldIcon = YpBlack,
    searchFieldText = YpBlack,
    searchFieldHint = YpTextGrey,
    inverseButton = White,
    inverseButtonText = YpBlack,
    bottomSheet = YpBlack,
    bottomSheetHandle = YpLightGrey,
    switchThumbChecked = YpBlue,
    switchTrackChecked = YpLightBlue,
    switchThumbUnchecked = YpTextGrey,
    switchTrackUnchecked = YpLightGrey,
    playerSecondaryButton = Color(0x40FFFFFF),
    navigationSelected = YpBlue,
    navigationUnselected = White,
    accent = YpBlue,
    inactive = YpTextGrey,
    overlay = YpOverlay,
)
