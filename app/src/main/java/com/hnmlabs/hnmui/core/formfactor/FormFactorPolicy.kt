package com.hnmlabs.hnmui.core.formfactor

import android.content.pm.ActivityInfo
import androidx.compose.runtime.Immutable
import com.hnmlabs.hnmui.model.LauncherCustomizationSettings
import kotlin.math.roundToInt

/**
 * HNMUI özellik bayrakları.
 * İlk aşamada telefon deneyimine odaklanılır; tablet düzeni mimaride rezerve tutulur.
 */
@Immutable
data class LauncherFeatureFlags(
    val enableTabletLayout: Boolean = true
)

enum class DeviceProfile {
    PHONE_ACTIVE,
    TABLET_RESERVED
}

/**
 * Ana ekran, Dock, klasörler ve Today View için tüm ölçü ve ızgara kurallarını soyutlar.
 * Kullanıcının 4x5'ten 7x7'ye kadar seçtiği ızgara, klasör ve Dock ölçeklerini ekran
 * genişliğine taşma yapmayacak şekilde matematiksel olarak dengeler.
 */
@Immutable
data class LauncherGridSpec(
    val profile: DeviceProfile,
    val isTabletHardwareDetected: Boolean,
    val columns: Int,
    val rows: Int,
    val dockVisible: Boolean,
    val dockMaxItems: Int,
    val iconSizeDp: Int,
    val dockIconSizeDp: Int,
    val dockScaleFactor: Float,
    val showHomeLabels: Boolean,
    val horizontalPaddingDp: Int,
    val folderPreviewColumns: Int,
    val folderExpandedColumns: Int,
    val glassOpacityFactor: Float,
    val todayDualColumn: Boolean,
    val dockShowRecentsDivider: Boolean
) {
    val maxItemsPerPage: Int get() = columns * rows
}

object FormFactorPolicy {
    private const val TABLET_SW_THRESHOLD_DP = 600

    fun resolve(
        smallestScreenWidthDp: Int,
        flags: LauncherFeatureFlags = LauncherFeatureFlags(),
        customization: LauncherCustomizationSettings = LauncherCustomizationSettings(),
        isLandscape: Boolean = false,
        screenWidthDp: Int = smallestScreenWidthDp,
        screenHeightDp: Int = smallestScreenWidthDp
    ): LauncherGridSpec {
        val safeSwDp = smallestScreenWidthDp.coerceAtLeast(320)
        val isTabletHardware = safeSwDp >= TABLET_SW_THRESHOLD_DP
        val glassFactor = (customization.glassOpacityPercent.coerceIn(70, 135)) / 100f

        if (isTabletHardware && flags.enableTabletLayout) {
            if (isLandscape) {
                // Tablet Yatay (Landscape) Modu
                val cols = customization.tabletLandscapeColumns.coerceIn(6, 8)
                val rows = customization.tabletLandscapeRows.coerceIn(4, 6)
                val dockCount = 6.coerceAtLeast(customization.dockMaxItems).coerceIn(6, 8)
                val horizPad = 48.coerceAtLeast((screenWidthDp * 0.05f).roundToInt())

                return LauncherGridSpec(
                    profile = DeviceProfile.TABLET_RESERVED,
                    isTabletHardwareDetected = true,
                    columns = cols,
                    rows = rows,
                    dockVisible = customization.dockVisible,
                    dockMaxItems = dockCount,
                    iconSizeDp = 68,
                    dockIconSizeDp = 64,
                    dockScaleFactor = 1.05f,
                    showHomeLabels = customization.showHomeLabels,
                    horizontalPaddingDp = horizPad,
                    folderPreviewColumns = 3,
                    folderExpandedColumns = 4,
                    glassOpacityFactor = glassFactor,
                    todayDualColumn = true,
                    dockShowRecentsDivider = true
                )
            } else {
                // Tablet Dikey (Portrait) Modu
                val cols = 6
                val rows = 5
                val dockCount = 6.coerceAtLeast(customization.dockMaxItems).coerceIn(6, 8)
                return LauncherGridSpec(
                    profile = DeviceProfile.TABLET_RESERVED,
                    isTabletHardwareDetected = true,
                    columns = cols,
                    rows = rows,
                    dockVisible = customization.dockVisible,
                    dockMaxItems = dockCount,
                    iconSizeDp = 64,
                    dockIconSizeDp = 62,
                    dockScaleFactor = 1f,
                    showHomeLabels = customization.showHomeLabels,
                    horizontalPaddingDp = 36,
                    folderPreviewColumns = 3,
                    folderExpandedColumns = 4,
                    glassOpacityFactor = glassFactor,
                    todayDualColumn = true,
                    dockShowRecentsDivider = true
                )
            }
        }

        if (isLandscape) {
            // Telefon Yatay (Landscape) Modu
            val cols = 6
            val rows = 3
            val dockCount = customization.dockMaxItems.coerceIn(3, 6)
            return LauncherGridSpec(
                profile = DeviceProfile.PHONE_ACTIVE,
                isTabletHardwareDetected = isTabletHardware,
                columns = cols,
                rows = rows,
                dockVisible = customization.dockVisible,
                dockMaxItems = dockCount,
                iconSizeDp = 48,
                dockIconSizeDp = 50,
                dockScaleFactor = 0.9f,
                showHomeLabels = customization.showHomeLabels,
                horizontalPaddingDp = 44,
                folderPreviewColumns = 3,
                folderExpandedColumns = 4,
                glassOpacityFactor = glassFactor,
                todayDualColumn = true,
                dockShowRecentsDivider = false
            )
        }

        val cols = customization.gridColumns.coerceIn(4, 7)
        val rows = customization.gridRows.coerceIn(4, 7)
        val horizPad = when {
            cols <= 4 -> 20
            cols == 5 -> 15
            cols == 6 -> 11
            else -> 8
        }

        val availableGridWidthDp = (safeSwDp - horizPad * 2).coerceAtLeast(280)
        val maxCellIconDp = (availableGridWidthDp / cols) - 10

        val baseIconDp = when (cols) {
            4 -> 60
            5 -> 51
            6 -> 43
            else -> 37
        }
        val iconScale = customization.iconScalePercent.coerceIn(80, 120) / 100f
        val computedIconDp = (baseIconDp * iconScale)
            .roundToInt()
            .coerceIn(28, maxCellIconDp.coerceAtLeast(28))

        val dockCount = customization.dockMaxItems.coerceIn(3, 7)
        val dockScale = customization.dockScalePercent.coerceIn(75, 125) / 100f
        val dockOuterMarginDp = (16f / dockScale).roundToInt().coerceIn(8, 22)
        val dockInnerPadDp = (16f * dockScale).roundToInt().coerceIn(10, 22)
        val availableDockWidthDp = (safeSwDp - dockOuterMarginDp * 2 - dockInnerPadDp * 2)
            .coerceAtLeast(240)
        val maxDockSlotIconDp = (availableDockWidthDp / dockCount) - 8
        val computedDockIconDp = (58f * dockScale)
            .roundToInt()
            .coerceIn(28, maxDockSlotIconDp.coerceAtLeast(28))

        return LauncherGridSpec(
            profile = DeviceProfile.PHONE_ACTIVE,
            isTabletHardwareDetected = isTabletHardware,
            columns = cols,
            rows = rows,
            dockVisible = customization.dockVisible,
            dockMaxItems = dockCount,
            iconSizeDp = computedIconDp,
            dockIconSizeDp = computedDockIconDp,
            dockScaleFactor = dockScale,
            showHomeLabels = customization.showHomeLabels,
            horizontalPaddingDp = horizPad,
            folderPreviewColumns = customization.folderPreviewColumns.coerceIn(2, 3),
            folderExpandedColumns = customization.folderColumns.coerceIn(3, 7),
            glassOpacityFactor = glassFactor,
            todayDualColumn = false,
            dockShowRecentsDivider = false
        )
    }

    /**
     * Telefon ve tabletlerde serbest ekran döndürme ve yönelim politikasını belirler.
     */
    fun preferredScreenOrientation(
        flags: LauncherFeatureFlags = LauncherFeatureFlags(),
        customization: LauncherCustomizationSettings = LauncherCustomizationSettings()
    ): Int {
        return if (flags.enableTabletLayout || customization.allowScreenRotation) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
