package com.hnmlabs.hnmui.model

import androidx.compose.runtime.Immutable
import com.hnmlabs.hnmui.core.text.PreparedSearchQuery
import com.hnmlabs.hnmui.core.text.TurkishSearchNormalizer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Bir Android aktivitesini çoklu kullanıcı profiliyle birlikte benzersiz tanımlayan anahtar.
 */
@Immutable
@Serializable
data class AppComponentKey(
    val packageName: String,
    val className: String,
    val userSerial: Long = 0L
) {
    @Transient
    val stableId: String = "$packageName/$className#$userSerial"
}

/**
 * App Library (Uygulama Arşivi) otomatik kategori sınıfları.
 */
enum class AppCategory(val titleTr: String) {
    SUGGESTED("Önerilenler"),
    RECENTLY_ADDED("Son Eklenenler"),
    SOCIAL("Sosyal"),
    PRODUCTIVITY("Üretkenlik ve Finans"),
    MEDIA("Eğlence ve Medya"),
    UTILITIES("Araçlar ve Sistem"),
    GAMES("Oyunlar"),
    OTHER("Diğer")
}

/**
 * Çalışma zamanında Launcher içinde gösterilen uygulama modeli.
 * Arama indeksleri (foldedLabel, asciiLabel, asciiPackage) oluşturma anında tek sefer hesaplanır;
 * böylece Spotlight, Uygulama Arşivi ve HNM Ayarlar aramalarında tuş başına sıfır tahsis yapılır.
 */
@Immutable
data class AppItem(
    val key: AppComponentKey,
    val label: String,
    val category: AppCategory,
    val firstInstallTimeMillis: Long = 0L,
    val isSystemApp: Boolean = false,
    val badgeCount: Int = 0,
    val foldedLabel: String = TurkishSearchNormalizer.foldTurkishI(label),
    val asciiLabel: String = TurkishSearchNormalizer.foldAscii(label),
    val foldedPackage: String = TurkishSearchNormalizer.foldTurkishI(key.packageName),
    val asciiPackage: String = TurkishSearchNormalizer.foldAscii(key.packageName)
) {
    fun matchesPreparedQuery(prepared: PreparedSearchQuery): Boolean {
        if (prepared.isEmpty) return true
        return TurkishSearchNormalizer.containsPreFolded(foldedLabel, asciiLabel, prepared) ||
            TurkishSearchNormalizer.containsPreFolded(foldedPackage, asciiPackage, prepared)
    }

    fun labelStartsWithPreparedQuery(prepared: PreparedSearchQuery): Boolean {
        return TurkishSearchNormalizer.startsWithPreFolded(foldedLabel, asciiLabel, prepared)
    }
}

/**
 * İkona uzun basıldığında (Haptic Touch) veya Spotlight aramasında gösterilen kısayol modeli.
 */
@Immutable
data class AppShortcutItem(
    val id: String,
    val packageName: String,
    val userSerial: Long,
    val shortLabel: String,
    val longLabel: String? = null
)

/**
 * Ana ekran sayfalarında yer alabilen öğeler: Tekil uygulama veya buzlu cam klasör.
 */
@Immutable
@Serializable
sealed interface HomeGridItem {
    val itemId: String

    @Immutable
    @Serializable
    @SerialName("app")
    data class AppTile(
        val key: AppComponentKey
    ) : HomeGridItem {
        override val itemId: String get() = "app:${key.stableId}"
    }

    @Immutable
    @Serializable
    @SerialName("folder")
    data class FolderTile(
        val id: String,
        val title: String,
        val apps: List<AppComponentKey>
    ) : HomeGridItem {
        override val itemId: String get() = "folder:$id"
    }
}

@Immutable
@Serializable
data class HomePageModel(
    val id: String,
    val items: List<HomeGridItem>
)

@Immutable
@Serializable
data class QuickNoteItem(
    val id: String,
    val text: String,
    val isDone: Boolean = false
)

/**
 * HNMUI profesyonel sistem görünüm modu:
 * - SYSTEM: Android sistem Koyu / Açık modunu canlı takip eder (varsayılan)
 * - LIGHT: Gün Işığı Sedefli Cam (Daylight Pearlescent Glass)
 * - DARK: Gece Yarısı Buzlu Cam (Midnight Frosted Glass)
 */
@Serializable
enum class HnmThemeMode(val titleTr: String, val shortTitleTr: String) {
    @SerialName("system")
    SYSTEM(titleTr = "Sistem ile Uyumlu", shortTitleTr = "Otomatik"),

    @SerialName("light")
    LIGHT(titleTr = "Açık Mod", shortTitleTr = "Açık"),

    @SerialName("dark")
    DARK(titleTr = "Koyu Mod", shortTitleTr = "Koyu");

    fun nextMode(): HnmThemeMode = when (this) {
        SYSTEM -> LIGHT
        LIGHT -> DARK
        DARK -> SYSTEM
    }
}

/**
 * iOS 18 tarzı saf buzlu cam ikon temalama stilleri:
 * - ORIGINAL: Standart orijinal renkli uygulama ikonları
 * - GLASS_AUTO: Sistem temasına (Açık/Koyu) göre sedefli cam veya gece camı
 * - GLASS_PEARL: Gün ışığı sedefli açık buzlu cam
 * - GLASS_MIDNIGHT: Gece yarısı koyu buzlu cam
 * - GLASS_TINTED: Özel seçilen vurgu rengiyle cam dokusu
 */
@Serializable
enum class IconTintStyle(val titleTr: String, val shortTitleTr: String) {
    @SerialName("original")
    ORIGINAL(titleTr = "Orijinal İkonlar", shortTitleTr = "Orijinal"),

    @SerialName("glass_auto")
    GLASS_AUTO(titleTr = "Buzlu Cam (Otomatik)", shortTitleTr = "Otomatik"),

    @SerialName("glass_pearl")
    GLASS_PEARL(titleTr = "Sedefli Açık Cam", shortTitleTr = "Açık"),

    @SerialName("glass_midnight")
    GLASS_MIDNIGHT(titleTr = "Gece Koyu Cam", shortTitleTr = "Koyu"),

    @SerialName("glass_tinted")
    GLASS_TINTED(titleTr = "Vurgu Renkli Cam", shortTitleTr = "Vurgu");

    fun nextStyle(): IconTintStyle = when (this) {
        ORIGINAL -> GLASS_AUTO
        GLASS_AUTO -> GLASS_PEARL
        GLASS_PEARL -> GLASS_MIDNIGHT
        GLASS_MIDNIGHT -> GLASS_TINTED
        GLASS_TINTED -> ORIGINAL
    }
}

/**
 * HNMUI profesyonel cam ve arayüz renk temaları:
 * - SAPPHIRE: Kozmik Safir (İkonik iOS elektrik mavisi)
 * - EMERALD: Zümrüt Kristali (Canlı yeşil cam parıltısı)
 * - AMBER: Gün Batımı Amberi (Sıcak altın ve portakal tonları)
 * - AMETHYST: Ametist Parıltısı (Fütüristik neon mor cam)
 * - RUBY: Yakut Neonu (Canlı yakut kırmızısı ve pembe)
 * - GLACIER: Buzul Kristali (Kutup mavisi berrak cam)
 * - OBSIDIAN: Obsidiyen & Platin (Saf minimalist siyah-gümüş cam)
 */
@Serializable
enum class HnmAccentTheme(
    val titleTr: String,
    val shortTitleTr: String,
    val primaryColorArgb: Long,
    val secondaryColorArgb: Long,
    val descriptionTr: String
) {
    @SerialName("sapphire")
    SAPPHIRE(
        titleTr = "Kozmik Safir",
        shortTitleTr = "Safir",
        primaryColorArgb = 0xFF0A84FFL,
        secondaryColorArgb = 0xFF38BDF8L,
        descriptionTr = "İkonik iOS elektrik mavisi ve berrak gökyüzü camı"
    ),

    @SerialName("emerald")
    EMERALD(
        titleTr = "Zümrüt Kristali",
        shortTitleTr = "Zümrüt",
        primaryColorArgb = 0xFF30D158L,
        secondaryColorArgb = 0xFF34D399L,
        descriptionTr = "Dingin yeşil parıltılı lüks buzlu cam estetiği"
    ),

    @SerialName("amber")
    AMBER(
        titleTr = "Gün Batımı Amberi",
        shortTitleTr = "Amber",
        primaryColorArgb = 0xFFFF9F0AL,
        secondaryColorArgb = 0xFFFB923CL,
        descriptionTr = "Sıcak altın ve gün batımı kehribar tonları"
    ),

    @SerialName("amethyst")
    AMETHYST(
        titleTr = "Ametist Parıltısı",
        shortTitleTr = "Ametist",
        primaryColorArgb = 0xFFAF52DEL,
        secondaryColorArgb = 0xFFC084FCL,
        descriptionTr = "Fütüristik derin mor ve neon cam yansımaları"
    ),

    @SerialName("ruby")
    RUBY(
        titleTr = "Yakut Neonu",
        shortTitleTr = "Yakut",
        primaryColorArgb = 0xFFFF375FL,
        secondaryColorArgb = 0xFFFB7185L,
        descriptionTr = "Canlı yakut kırmızısı ve enerjik gül kurusu cam"
    ),

    @SerialName("glacier")
    GLACIER(
        titleTr = "Buzul Kristali",
        shortTitleTr = "Buzul",
        primaryColorArgb = 0xFF64D2FFL,
        secondaryColorArgb = 0xFF38BDF8L,
        descriptionTr = "Kutup mavisi berrak kristal cam dokusu"
    ),

    @SerialName("obsidian")
    OBSIDIAN(
        titleTr = "Obsidiyen & Platin",
        shortTitleTr = "Platin",
        primaryColorArgb = 0xFFE2E8F0L,
        secondaryColorArgb = 0xFF94A3B8L,
        descriptionTr = "Saf minimalist siyah cam ve platin gümüş tonlar"
    );

    fun nextTheme(): HnmAccentTheme {
        val entries = entries
        val idx = entries.indexOf(this)
        return entries[(idx + 1) % entries.size]
    }
}

/**
 * Kullanıcının HNM Ayarlar içerisinden değiştirebildiği minimalist arayüz,
 * ızgara (7x7'ye kadar), klasör, Dock, görünüm modu, tasarım renk teması,
 * buzlu cam ikon teması ve Akıllı Cam Kapsül (Dynamic Glass Island) tercihleri.
 */
@Immutable
@Serializable
data class LauncherCustomizationSettings(
    val gridColumns: Int = 4,
    val gridRows: Int = 6,
    val iconScalePercent: Int = 100,
    val showHomeLabels: Boolean = true,
    val folderColumns: Int = 3,
    val folderPreviewColumns: Int = 2,
    val dockVisible: Boolean = true,
    val dockMaxItems: Int = 4,
    val dockScalePercent: Int = 100,
    val glassOpacityPercent: Int = 100,
    val interceptSystemSettingsApp: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val useIosStatusBar: Boolean = true,
    val themeMode: HnmThemeMode = HnmThemeMode.SYSTEM,
    val accentTheme: HnmAccentTheme = HnmAccentTheme.SAPPHIRE,
    val iconTintStyle: IconTintStyle = IconTintStyle.ORIGINAL,
    val iconAccentColorRgb: Long = 0xFF0A84FFL,
    val dynamicIslandEnabled: Boolean = true,
    val allowScreenRotation: Boolean = true,
    val tabletLandscapeColumns: Int = 6,
    val tabletLandscapeRows: Int = 4
)

/**
 * DataStore üzerinde JSON olarak kalıcı saklanan kullanıcı ana ekran düzeni ve ayarları.
 */
@Immutable
@Serializable
data class PersistedHomeState(
    val dock: List<AppComponentKey> = emptyList(),
    val pages: List<HomePageModel> = emptyList(),
    val quickNotes: List<QuickNoteItem> = emptyList(),
    val boundWidgetIds: List<Int> = emptyList(),
    val launchCounts: Map<String, Int> = emptyMap(),
    val customization: LauncherCustomizationSettings = LauncherCustomizationSettings(),
    val hasInjectedHnmSettingsTile: Boolean = false
)

/**
 * Bildirim Merkezi katmanında gösterilen canlı bildirim kartı modeli.
 */
@Immutable
data class LiveNotificationCard(
    val key: String,
    val packageName: String,
    val title: String,
    val message: String,
    val postTimeMillis: Long
)

/**
 * Çözümlenmiş (AppItem nesneleriyle eşleştirilmiş) UI katmanı ana ekran öğeleri.
 */
@Immutable
sealed interface ResolvedHomeItem {
    val itemId: String

    @Immutable
    data class App(val app: AppItem) : ResolvedHomeItem {
        override val itemId: String = "app:${app.key.stableId}"
    }

    @Immutable
    data class Folder(
        val id: String,
        val title: String,
        val apps: List<AppItem>,
        val totalBadges: Int
    ) : ResolvedHomeItem {
        override val itemId: String = "folder:$id"
    }
}

@Immutable
data class ResolvedHomePage(
    val id: String,
    val items: List<ResolvedHomeItem>
)

@Immutable
data class AppLibraryGroup(
    val category: AppCategory,
    val apps: List<AppItem>
)
