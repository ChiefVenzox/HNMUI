package com.hnmlabs.hnmui.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BrightnessHigh
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.hnmlabs.hnmui.model.HnmThemeMode
import com.hnmlabs.hnmui.ui.theme.HnmMotion
import com.hnmlabs.hnmui.ui.theme.HnmPalette
import com.hnmlabs.hnmui.ui.theme.HnmTheme
import kotlinx.coroutines.delay

/**
 * Akıllı Cam Kapsül'ün (Dynamic Glass Island) anlık görsel durumu.
 */
enum class DynamicIslandVisualState {
    COMPACT,
    ALERT_CHARGING,
    ALERT_VOLUME,
    ALERT_BRIGHTNESS,
    ALERT_THEME,
    EXPANDED
}

/**
 * Akıllı Cam Kapsül'ü tetikleyen canlı olay modeli.
 */
@Immutable
sealed interface DynamicIslandAlertEvent {
    @Immutable
    data class Charging(val percent: Int) : DynamicIslandAlertEvent

    @Immutable
    data class Volume(val fraction: Float) : DynamicIslandAlertEvent

    @Immutable
    data class Brightness(val fraction: Float) : DynamicIslandAlertEvent

    @Immutable
    data class ThemeChanged(val mode: HnmThemeMode) : DynamicIslandAlertEvent
}

/**
 * HNMUI Akıllı Cam Kapsül (Dynamic Glass Island).
 *
 * Ekranın üst çentik / kamera deliği hizasında yer alan, iOS tarzı dinamik yay fiziğine
 * ve HNM Sedefli / Gece Koyu buzlu cam dokusuna sahip interaktif bildirim ve kontrol kapsülü.
 *
 * - Şarj takıldığında yeşil neon şimşek ve canlı pil yüzdesi ile yay gibi genişler.
 * - Ses / parlaklık değiştiğinde mini seviye göstergesi sunar.
 * - Tema modları arasında geçiş yapıldığında güneş/ay animasyonuyla genişler.
 * - Üzerine dokunulduğunda tam interaktif mikro kontrol kartına dönüşür (120 FPS GPU draw-phase).
 */
@Composable
fun DynamicGlassIsland(
    batteryPercent: Int,
    isCharging: Boolean,
    themeMode: HnmThemeMode,
    onOpenControlCenter: () -> Unit,
    onOpenNotificationCenter: () -> Unit,
    onToggleThemeMode: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = HnmTheme.colors
    val haptics = LocalHapticFeedback.current

    var visualState by remember { mutableStateOf(DynamicIslandVisualState.COMPACT) }
    var alertPayloadText by remember { mutableStateOf("") }
    var alertIcon by remember { mutableStateOf<ImageVector>(Icons.Rounded.BatteryChargingFull) }
    var alertAccentColor by remember { mutableStateOf(Color(0xFF30D158)) }
    var alertProgressFraction by remember { mutableFloatStateOf(1f) }

    // Şarj bağlantı durumunu canlı izle ve otomatik uyar
    var prevChargingState by remember { mutableStateOf(isCharging) }
    var prevBatteryPercent by remember { mutableIntStateOf(batteryPercent) }

    LaunchedEffect(isCharging, batteryPercent) {
        if (isCharging && !prevChargingState) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            alertIcon = Icons.Rounded.BatteryChargingFull
            alertAccentColor = Color(0xFF30D158)
            alertPayloadText = "%$batteryPercent Şarj Ediliyor"
            alertProgressFraction = (batteryPercent / 100f).coerceIn(0f, 1f)
            visualState = DynamicIslandVisualState.ALERT_CHARGING
        }
        prevChargingState = isCharging
        prevBatteryPercent = batteryPercent
    }

    // Geçici uyarı durumlarında 2.8 saniye sonra otomatik olarak kompakt kapsüle dön
    LaunchedEffect(visualState) {
        if (visualState in setOf(
                DynamicIslandVisualState.ALERT_CHARGING,
                DynamicIslandVisualState.ALERT_VOLUME,
                DynamicIslandVisualState.ALERT_BRIGHTNESS,
                DynamicIslandVisualState.ALERT_THEME
            )
        ) {
            delay(2800L)
            visualState = DynamicIslandVisualState.COMPACT
        }
    }

    // Kamera çentiği ve güvenli durum çubuğu mesafesi
    val cutoutTop = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topInsetDp = max(cutoutTop, statusBarTop)
    val effectiveTopMargin = (topInsetDp + 6.dp).coerceAtLeast(10.dp)

    // Yay fiziğiyle genişlik, yükseklik ve köşe yuvarlatma hesaplaması
    val isExpanded = visualState == DynamicIslandVisualState.EXPANDED
    val isAlert = visualState in setOf(
        DynamicIslandVisualState.ALERT_CHARGING,
        DynamicIslandVisualState.ALERT_VOLUME,
        DynamicIslandVisualState.ALERT_BRIGHTNESS,
        DynamicIslandVisualState.ALERT_THEME
    )

    val targetWidth = when {
        isExpanded -> 332.dp
        isAlert -> 228.dp
        else -> 116.dp
    }
    val targetHeight = when {
        isExpanded -> 138.dp
        isAlert -> 38.dp
        else -> 28.dp
    }
    val targetCorner = when {
        isExpanded -> 26.dp
        isAlert -> 19.dp
        else -> 14.dp
    }

    val animatedWidth by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = HnmMotion.sheetSpring(),
        label = "islandWidth"
    )
    val animatedHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = HnmMotion.sheetSpring(),
        label = "islandHeight"
    )
    val animatedCorner by animateDpAsState(
        targetValue = targetCorner,
        animationSpec = HnmMotion.sheetSpring(),
        label = "islandCorner"
    )

    // Buzlu cam fırçaları
    val islandFillBrush = remember(colors.isDark, isExpanded) {
        if (colors.isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xEE0B0F19),
                    Color(0xF6030712)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xF5FFFFFF),
                    Color(0xEEF1F5F9)
                )
            )
        }
    }

    val islandBorderBrush = remember(colors.isDark, isAlert, alertAccentColor) {
        if (isAlert) {
            Brush.verticalGradient(
                listOf(
                    alertAccentColor.copy(alpha = 0.85f),
                    alertAccentColor.copy(alpha = 0.35f),
                    Color(0x22FFFFFF)
                )
            )
        } else if (colors.isDark) {
            Brush.verticalGradient(
                listOf(
                    Color(0x55FFFFFF),
                    Color(0x1AFFFFFF),
                    Color(0x33000000)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0x99FFFFFF),
                    Color(0x3394A3B8),
                    Color(0x22CBD5E1)
                )
            )
        }
    }

    val shape = RoundedCornerShape(animatedCorner)

    Box(
        modifier = modifier
            .padding(top = effectiveTopMargin)
            .width(animatedWidth)
            .height(animatedHeight)
            .clip(shape)
            .background(islandFillBrush, shape)
            .border(width = 1.dp, brush = islandBorderBrush, shape = shape)
            .pointerInput(visualState) {
                if (visualState == DynamicIslandVisualState.EXPANDED) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -15f) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            visualState = DynamicIslandVisualState.COMPACT
                        }
                    }
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                visualState = if (visualState == DynamicIslandVisualState.EXPANDED) {
                    DynamicIslandVisualState.COMPACT
                } else {
                    DynamicIslandVisualState.EXPANDED
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = visualState,
            transitionSpec = {
                fadeIn(animationSpec = HnmMotion.sheetSpring()) togetherWith
                    fadeOut(animationSpec = HnmMotion.sheetSpring())
            },
            label = "islandContentTransition"
        ) { targetVisual ->
            when (targetVisual) {
                DynamicIslandVisualState.COMPACT -> {
                    // Kamera deliğiyle bütünleşen minimalist kompakt kapsül
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Sol micro sensör / durum noktası
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCharging) Color(0xFF30D158) else colors.textMuted.copy(alpha = 0.5f)
                                )
                        )

                        // Sağ pil / seviye özeti
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "%$batteryPercent",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (colors.isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
                            )
                            if (isCharging) {
                                Icon(
                                    imageVector = Icons.Rounded.BatteryChargingFull,
                                    contentDescription = null,
                                    tint = Color(0xFF30D158),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }

                DynamicIslandVisualState.ALERT_CHARGING,
                DynamicIslandVisualState.ALERT_VOLUME,
                DynamicIslandVisualState.ALERT_BRIGHTNESS,
                DynamicIslandVisualState.ALERT_THEME -> {
                    // Canlı bildirim / geçiş uyarısı modu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(alertAccentColor.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = alertIcon,
                                    contentDescription = null,
                                    tint = alertAccentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = alertPayloadText,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (colors.isDark) Color.White else Color(0xFF0F172A)
                            )
                        }

                        // Mini progress kapsülü
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(colors.surfaceSubtleFill)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(alertProgressFraction)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(alertAccentColor)
                            )
                        }
                    }
                }

                DynamicIslandVisualState.EXPANDED -> {
                    // Genişletilmiş interaktif buzlu cam mikro kontrol kartı
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Üst Başlık Satırı
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(HnmPalette.AccentBlue, HnmPalette.AccentCyan)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Tune,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "HNM Akıllı Kapsül",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Pil durumu hapı
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isCharging) Color(0x3330D158) else colors.controlInactiveFill
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.BatteryChargingFull,
                                        contentDescription = null,
                                        tint = if (isCharging) Color(0xFF30D158) else colors.textMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "%$batteryPercent",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isCharging) Color(0xFF30D158) else colors.textPrimary
                                    )
                                }

                                // Kapatma butonu
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(colors.controlInactiveFill)
                                        .clickable {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            visualState = DynamicIslandVisualState.COMPACT
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Kapat",
                                        tint = colors.textMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        // Alt Hızlı Aksiyon Satırı
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Görünüm Modu Değiştirme Butonu
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.controlInactiveFill)
                                    .clickable {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onToggleThemeMode?.invoke()
                                    }
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (colors.isDark) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                                    contentDescription = null,
                                    tint = if (colors.isDark) HnmPalette.AccentBlue else Color(0xFFFF9F0A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = themeMode.shortTitleTr,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            }

                            // Bildirim Merkezi Kısayolu
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.controlInactiveFill)
                                    .clickable {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        visualState = DynamicIslandVisualState.COMPACT
                                        onOpenNotificationCenter()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Notifications,
                                    contentDescription = "Bildirim Merkezi",
                                    tint = colors.textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Denetim Merkezi Kısayolu
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(HnmPalette.AccentBlue, HnmPalette.AccentCyan)
                                        )
                                    )
                                    .clickable {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        visualState = DynamicIslandVisualState.COMPACT
                                        onOpenControlCenter()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Tune,
                                    contentDescription = "Denetim Merkezi",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
