package com.hnmlabs.hnmui.ui.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hnmlabs.hnmui.model.AppComponentKey
import com.hnmlabs.hnmui.model.AppItem
import com.hnmlabs.hnmui.ui.components.AppIconTile
import com.hnmlabs.hnmui.ui.components.GlassSurface
import com.hnmlabs.hnmui.ui.components.GlassTone
import com.hnmlabs.hnmui.ui.components.SquircleShape
import com.hnmlabs.hnmui.ui.theme.HnmMotion
import com.hnmlabs.hnmui.ui.theme.HnmTheme
import kotlin.math.roundToInt

/**
 * Yeniden tasarlanmış, üst seviye iOS / VisionOS estetiğinde buzlu cam klasör katmanı.
 *
 * Özellikler:
 * - Sayfalandırılmış 3x3 / 4x4 iOS klasör ızgarası (HorizontalPager)
 * - Akıcı yay animasyonlu sayfa kapsülleri (indicator dots)
 * - İnteraktif başlık düzenleme cam kapsülü (inline rename)
 * - Aşağı kaydırarak veya arka plana dokunarak kapatma jesti
 * - Çift katmanlı prizmatik cam kenarlıklar ve ambient derinlik gölgesi
 */
@Composable
fun FolderModalOverlay(
    visible: Boolean,
    title: String,
    isEditableTitle: Boolean,
    columns: Int,
    showLabels: Boolean = true,
    apps: List<AppItem>,
    cachedIconProvider: (AppComponentKey) -> ImageBitmap?,
    loadIconSuspend: suspend (AppComponentKey) -> ImageBitmap?,
    onRenameTitle: (String) -> Unit,
    onLaunchApp: (AppItem) -> Unit,
    onAppLongPress: (AppItem) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = HnmTheme.colors
    val haptics = LocalHapticFeedback.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = HnmMotion.folderSpring()) +
            scaleIn(initialScale = 0.82f, animationSpec = HnmMotion.folderSpring()) +
            slideInVertically(
                initialOffsetY = { it / 14 },
                animationSpec = HnmMotion.folderSpring()
            ),
        exit = fadeOut(animationSpec = HnmMotion.folderSpring()) +
            scaleOut(targetScale = 0.82f, animationSpec = HnmMotion.folderSpring()) +
            slideOutVertically(
                targetOffsetY = { it / 14 },
                animationSpec = HnmMotion.folderSpring()
            )
    ) {
        val focusManager = LocalFocusManager.current
        var draftTitle by remember(title) { mutableStateOf(title) }
        var isEditingTitle by remember { mutableStateOf(false) }
        var dragDismissOffsetY by remember { mutableFloatStateOf(0f) }

        val safeCols = columns.coerceIn(3, 7)
        val pageSize = (safeCols * safeCols).coerceAtLeast(9)
        val pagedApps = remember(apps, pageSize) {
            val chunks = apps.chunked(pageSize)
            if (chunks.isEmpty()) listOf(emptyList()) else chunks
        }
        val pagerState = rememberPagerState(initialPage = 0, pageCount = { pagedApps.size })

        val folderCardMaxWidth = when {
            safeCols >= 6 -> 440.dp
            safeCols == 5 -> 380.dp
            safeCols == 4 -> 348.dp
            else -> 324.dp
        }

        val folderIconDp = when (safeCols) {
            3 -> 58.dp
            4 -> 48.dp
            5 -> 40.dp
            6 -> 34.dp
            else -> 29.dp
        }

        val innerGridPad = when (safeCols) {
            3 -> 18.dp
            4 -> 14.dp
            5 -> 10.dp
            else -> 8.dp
        }
        val hSpacing = when (safeCols) {
            3 -> 12.dp
            4 -> 8.dp
            else -> 6.dp
        }
        val vSpacing = when (safeCols) {
            3 -> 14.dp
            4 -> 10.dp
            else -> 8.dp
        }

        LaunchedEffect(visible) {
            if (visible) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                dragDismissOffsetY = 0f
            }
        }

        // Karartılmış buzlu cam arka plan (scrim)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.scrimOverlay)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (isEditableTitle && draftTitle != title) {
                            onRenameTitle(draftTitle)
                        }
                        onDismiss()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = folderCardMaxWidth)
                    .fillMaxWidth(0.90f)
                    .wrapContentHeight()
                    .offset { IntOffset(0, dragDismissOffsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (dragDismissOffsetY > 72f) {
                                    if (isEditableTitle && draftTitle != title) {
                                        onRenameTitle(draftTitle)
                                    }
                                    onDismiss()
                                } else {
                                    dragDismissOffsetY = 0f
                                }
                            },
                            onDragCancel = { dragDismissOffsetY = 0f },
                            onVerticalDrag = { _, dragAmount ->
                                if (dragAmount > 0 || dragDismissOffsetY > 0) {
                                    dragDismissOffsetY = (dragDismissOffsetY + dragAmount * 0.7f).coerceAtLeast(0f)
                                }
                            }
                        )
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (isEditingTitle) {
                                isEditingTitle = false
                                focusManager.clearFocus()
                                if (draftTitle != title) {
                                    onRenameTitle(draftTitle)
                                }
                            }
                        }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Üst Başlık Kapsülü
                if (isEditableTitle) {
                    GlassSurface(
                        shape = RoundedCornerShape(18.dp),
                        tone = GlassTone.PILL,
                        modifier = Modifier
                            .clickable {
                                isEditingTitle = true
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            .padding(horizontal = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            BasicTextField(
                                value = draftTitle,
                                onValueChange = {
                                    draftTitle = it
                                    isEditingTitle = true
                                },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 17.sp
                                ),
                                cursorBrush = SolidColor(colors.accentCyan),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        onRenameTitle(draftTitle)
                                        isEditingTitle = false
                                        focusManager.clearFocus()
                                    }
                                )
                            )
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Klasör adını değiştir",
                                tint = colors.accentCyan.copy(alpha = 0.85f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                } else {
                    GlassSurface(
                        shape = RoundedCornerShape(18.dp),
                        tone = GlassTone.PILL,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp
                            ),
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ana Klasör Kartı (Lüks Süper-Elips Buzlu Cam Kapsül)
                GlassSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 28.dp,
                            shape = SquircleShape.Card,
                            ambientColor = Color(0x66000000),
                            spotColor = Color(0x88000000)
                        ),
                    shape = SquircleShape.Card,
                    tone = GlassTone.ELEVATED_MODAL
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Üst minyatür kapatma çubuğu / hızlı kapat butonu
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, start = 14.dp, end = 14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 36.dp, height = 4.dp)
                                    .clip(CircleShape)
                                    .background(colors.subtleBorder.copy(alpha = 0.6f))
                                    .align(Alignment.Center)
                            )

                            IconButton(
                                onClick = {
                                    if (isEditableTitle && draftTitle != title) {
                                        onRenameTitle(draftTitle)
                                    }
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Klasörü Kapat",
                                    tint = colors.textSecondary.copy(alpha = 0.75f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Sayfalandırılmış ızgara alanı
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 250.dp, max = if (safeCols >= 6) 420.dp else 330.dp)
                        ) { pageIdx ->
                            val currentItems = pagedApps.getOrElse(pageIdx) { emptyList() }
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(safeCols),
                                contentPadding = PaddingValues(innerGridPad),
                                horizontalArrangement = Arrangement.spacedBy(hSpacing),
                                verticalArrangement = Arrangement.spacedBy(vSpacing),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(currentItems, key = { it.key.stableId }) { app ->
                                    AppIconTile(
                                        app = app,
                                        iconSize = folderIconDp,
                                        showLabel = if (safeCols >= 7) false else showLabels,
                                        onGlassSurface = true,
                                        cachedIconProvider = cachedIconProvider,
                                        loadIconSuspend = loadIconSuspend,
                                        onClick = { onLaunchApp(app) },
                                        onLongClick = { onAppLongPress(app) }
                                    )
                                }
                            }
                        }

                        // Klasör Çoklu Sayfa Gösterge Kapsülleri (iOS Sayfa Noktaları)
                        if (pagedApps.size > 1) {
                            Row(
                                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(pagedApps.size) { dotIdx ->
                                    val isCurrent = pagerState.currentPage == dotIdx
                                    val dotWidth by animateDpAsState(
                                        targetValue = if (isCurrent) 16.dp else 6.dp,
                                        animationSpec = HnmMotion.slotHighlightSpring(),
                                        label = "folderDotWidth_$dotIdx"
                                    )
                                    val dotColor by animateColorAsState(
                                        targetValue = if (isCurrent) colors.accentCyan else colors.textMuted.copy(alpha = 0.38f),
                                        animationSpec = HnmMotion.slotHighlightSpring(),
                                        label = "folderDotColor_$dotIdx"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(dotWidth)
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
