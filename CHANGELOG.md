# Changelog / Sürüm Geçmişi

All notable changes to **HNMUI** are documented in this file.  
**HNMUI** projesindeki tüm önemli değişiklikler bu dosyada kayıt altına alınır.

---

## [Unreleased] (Tablet Support, Horizontal/Landscape Adaptation & Design Innovations)

### 📱 Tablet & Landscape Mode Adaptation / Tablet & Yatay Mod Uyarlaması
- **EN:** Activated full tablet and horizontal screen support (`enableTabletLayout = true`, `allowScreenRotation = true`). Enabled fluid orientation changes (`SCREEN_ORIENTATION_UNSPECIFIED`) with zero cold restarts (`android:configChanges` orientation handling). Implemented iPadOS-style floating centered glass Dock (`LauncherBottomDockBar`) with `widthIn(max = 680.dp)`, responsive 6x4 landscape / 6x5 portrait tablet grids, a 2-column Today View dashboard (`todayDualColumn = true`), responsive 3–4 category columns in App Library (`AppLibraryPage`), and ergonomic centered or TopEnd floating sheet cards for Control Center, Notification Center, Spotlight, and HNM Settings. Added an "Otomatik Ekran Döndürme & Yatay Mod" toggle in HNM Settings.
- **TR:** Tam tablet ve yatay ekran deneyimi aktifleştirildi (`enableTabletLayout = true`, `allowScreenRotation = true`). Yeniden başlatma gerektirmeden (`onConfigurationChanged`) 120 FPS akıcılıkta ekran yönelim geçişleri sağlandı. iPadOS tarzı yüzen ortalanmış cam Dock (`widthIn(max = 680.dp)`), 6x4 yatay / 6x5 dikey tablet ızgaraları, 2 sütunlu Bugün Panosu (`todayDualColumn`), Uygulama Arşivi'nde 3-4 sütunlu kategori dizilimi ve Denetim Merkezi, Bildirim Merkezi, Spotlight ve Ayarlar panellerinde geniş ekran kart sınırlamaları eklendi. HNM Ayarlar'a "Otomatik Ekran Döndürme & Yatay Mod" açma/kapatma seçeneği getirildi.

### 🏝️ Dynamic Glass Island / Akıllı Cam Kapsül
- **EN:** Engineered an interactive, spring-animated frosted glass **Dynamic Glass Island** anchored at the top camera-cutout/status-bar strip. Fluidly expands with spring physics on live charging events (emerald glow + animated lightning bolt + `%XX` battery), volume/brightness feedback, and theme toggles, and expands on tap into an interactive micro-control island card (theme switch, volume, notifications, control center) with 120 FPS GPU draw-phase smoothness.
- **TR:** Ekranın üst kamera deliği ve durum çubuğu şeridine entegre, yay fiziğine sahip interaktif buzlu cam **Akıllı Cam Kapsül (Dynamic Glass Island)** geliştirildi. Canlı şarj bağlantısında (yeşil neon parlama + şimşek + canlı pil yüzdesi), ses/parlaklık geri bildiriminde ve tema geçişlerinde otomatik genişler; dokunulduğunda mikro kontrol kartına dönüşür (120 FPS GPU çizim katmanı).

### 🎨 Design Color Palettes & Dedicated Themes Settings / Tasarım Renk Temaları & Temalar Bölümü
- **EN:** Created a dedicated **"Temalar ve Renk Paleti"** section in HNM Settings (`SettingsRoute.THEMES`) featuring 7 curated frosted glass design color themes (`Kozmik Safir`, `Zümrüt Kristali`, `Gün Batımı Amberi`, `Ametist Parıltısı`, `Yakut Neonu`, `Buzul Kristali`, `Obsidiyen & Platin`). Includes an interactive **Live Glass Theme Preview Card** rendering mini capsules and glass icons, integrated system appearance mode toggle (`Otomatik` / `Açık` / `Koyu`), iOS 18 tinted icon style selector, and glass opacity depth slider.
- **TR:** HNM Ayarlar içerisine kapsamlı **"Temalar ve Renk Paleti"** sayfası eklendi (`SettingsRoute.THEMES`). 7 seçkin tasarım renk paleti (`Kozmik Safir`, `Zümrüt Kristali`, `Gün Batımı Amberi`, `Ametist Parıltısı`, `Yakut Neonu`, `Buzul Kristali`, `Obsidiyen & Platin`), minyatür kapsül ve cam ikonları simüle eden interaktif **Canlı Cam Önizleme Kartı**, Açık/Koyu görünüm modu seçici, buzlu cam ikon temalama motoru ve cam şeffaflık sürgüsü bir araya getirildi.

### 🔇 Control Center Silent/Sound Toggle & Resilient Ringer Fix / Denetim Merkezi Sessiz/Sesli Ayarı Düzeltmesi
- **EN:** Resolved the issue where sound/silent ringer mode could not be toggled from the Control Center. Added a dedicated **"Sessiz / Sesli" QuickToggleTile** in the 2x2 tool grid (`Icons.AutoMirrored.Rounded.VolumeOff` / `VolumeUp`) with live state tracking, tap-to-mute on the vertical volume slider icon, and built resilient audio stream fallback in `DeviceSystemController.toggleRingerMode` that seamlessly mutes/unmutes ring/notification streams even when Android Notification Policy / DND permission has not yet been granted.
- **TR:** Denetim Merkezi'nde sessiz/sesli modunun değiştirilememesi sorunu kökten çözüldü: 2x2 hızlı araçlar arasına canlı durum takipli özel **"Sessiz / Sesli" Hızlı Butonu** eklendi; dikey ses sürgüsü ikonuna doğrudan sessize alma/sesi açma tıklama desteği sağlandı. Android 7+ DND izin kısıtlamasına takılmadan ring/notification ses akışları üzerinden anında sessize alma ve ses açma yapabilen dayanıklı `DeviceSystemController.toggleRingerMode` motoru entegre edildi.

---

## [0.3.4-alpha] — 2026-11-13 (System Adaptive Glass & Zero-Miss Control Center)

### 🌗 System Adaptive Appearance Engine / Sistem Uyumlu Açık & Koyu Cam Motoru
- **EN:** Introduced flagship **`HnmThemeMode` (`SYSTEM` / `LIGHT` / `DARK`)** tracking Android's live system Dark/Light mode (`isSystemInDarkTheme()` via zero-restart `uiMode` handling) alongside instant manual overrides in **Control Center** (`Otomatik` / `Açık` / `Koyu`) and **HNM Settings** (*Home Screen, Folder & Dock* + *Display & Brightness*).
- **EN:** Engineered smooth **`380ms` cubic-bezier (`0.22, 1.0, 0.36, 1.0`) color & glass transitions** across **Midnight Dark Glass** and **Daylight Pearlescent Glass** (`GlassSurface` `DOCK` / `CARD` / `ELEVATED_MODAL` / `PILL`, overlays, typography, inputs, sliders, switches, and `WindowInsetsControllerCompat` status/navigation bar contrast) with **0% idle CPU cost** once settled.
- **EN:** Separated `AppIconTile` / `FolderIconTile` optical contrast (`onGlassSurface = true` crisp ink inside frosted sheets vs shadowed `onWallpaperPrimary` over live wallpapers) for full WCAG readability on both bright and dark wallpapers.
- **TR:** Android sisteminin Açık/Koyu modunu yeniden başlatma gerektirmeden canlı takip eden ve **Denetim Merkezi** ile **HNM Ayarlar** (*Ana Ekran, Klasör & Dock* ve *Ekran ve Parlaklık*) üzerinden anında değiştirilebilen **`HnmThemeMode` (`Otomatik` / `Açık` / `Koyu`)** motoru eklendi.
- **TR:** **Gece Koyu Cam** (*Midnight Dark Glass*) ile **Sedefli Açık Cam** (*Daylight Pearlescent Glass*) arasında tüm cam yüzeyler, kartlar, sürgüler, yazılar ve sistem çubukları boyunca **`380 ms` akıcı geçiş animasyonu** (boşta **%0 CPU** yükü) ve tam kontrast uyumu sağlandı.

### 🎯 Zero-Miss Control Center & Performance Polish / Kesintisiz Denetim Merkezi ve Performans
- **EN:** Rebuilt top/right pull-down classification (`classifyDownwardPull`) with a camera-cutout-aware **`86dp+` top strip across all pages & Edit Mode**, **`14dp` instant trigger threshold**, natural diagonal right-thumb arc tolerance (`dy >= dx * 0.72f`), expanded mid-right Home one-handed pull zone (`68%` height), full-half-width `HnmIosStatusBar` tap/pull targets, post-trigger pointer drain loop, auto-rehide of transient OEM status bars (`1.6s`), and non-blocking hit-testing on exiting overlays.
- **EN:** Eliminated idle per-slot `animateFloatAsState` + `graphicsLayer` allocations in `LauncherBottomDockBar` when not dragging and memoized `cameraApp` lookup in `NotificationCenterOverlay`.
- **TR:** Denetim Merkezi'nin bazen açılıp bazen açılmama sorunu kökten çözüldü: kamera deliği duyarlı **`86dp+` üst şerit** (tüm sayfalar + Düzenleme Modu), **`14dp` hızlı tetikleme**, sağ başparmak çapraz kavis toleransı, sağ kenar tek el çekme alanı (`%68` yükseklik), tam yarım ekran `HnmIosStatusBar` dokunma/çekme hedefleri, kapanan panellerde dokunma kilitlememe ve geçici sistem çubuğunu otomatik gizleme (`1.6 sn`) eklendi.
- **TR:** Sürükleme yokken alt Dock yuvalarındaki boşta animatör ve GPU katmanı tahsisatları sıfırlandı; Bildirim Merkezi kamera kısayolu araması önbelleğe alındı.

---

## [0.3.3-alpha] — 2026-11-13 (Zero-Freeze & 120 FPS Fluidity Engine)

### ⚡ Performance & Fluidity / Performans ve Sıfır Donma Motoru
- **EN:** Eliminated synchronous Main-Thread Binder/SQLite IPC stalls in `DeviceSystemController` (`setBrightnessFraction`, `setStreamVolumeFraction`, `toggleFlashlight`, `dispatchMediaKey`, `setAutoBrightness`, `setRotationLocked`, `setScreenTimeoutMs`) via conflated `Dispatchers.IO` pipelines and cached stream bounds.
- **EN:** Upgraded `LauncherScreen`, `HomeGridPage`, `AppIconTile`, and `LauncherDragDropController` to a pure **Draw-Phase 120Hz Pipeline**: `HorizontalPager` offset math, overlay background depth recession (`94.5%`), drag ghost scale, and shared `LocalEditJigglePhase` icon jiggle all execute inside `Modifier.graphicsLayer { ... }` with **zero composition/layout invalidations** and zero idle per-cell `Animatable` allocations.
- **EN:** Precomputed `SquircleShape` unit superellipse vertices with a thread-safe LRU `Outline` cache (zero `sin`/`cos`/`pow` or `Path` allocations while scrolling), hoisted `GlassSurface` border/fill brushes, and annotated all UI/domain state models `@Immutable`.
- **EN:** Decoupled `InstalledAppsRepository` cold-start app emission from cooperative background icon pre-warming (`512`-entry LRU cache + `inFlightIconLoads` deduplication + `45s` `onResume` scan throttle), moved `LauncherActivity` permission/role checks off the UI thread, cached decoded `HomeLayoutRepository` JSON on `Dispatchers.IO`, deferred `recordAppLaunch` by `450ms` after app start, and added `HnmSteppedCustomizationSlider` + pre-folded `PreparedSearchQuery` matching.
- **TR:** Denetim Merkezi ve HNM Ayarlar'daki parlaklık, ses, el feneri, medya ve döndürme kilidi işlemlerinin Main Thread üzerindeki senkron Binder/SQLite IPC blokajları tamamen kaldırıldı (`Dispatchers.IO` conflated akış mimarisi + yerel sürgü takibi).
- **TR:** `LauncherScreen`, `HomeGridPage`, `AppIconTile` ve `LauncherDragDropController` tamamen **Draw-Phase (GPU Çizim Katmanı)** mimarisine geçirildi: sayfa kaydırma (`HorizontalPager`), arka plan derinlik çekilmesi, sürükle-bırak ve tek saatli (`LocalEditJigglePhase`) jiggle animasyonları **sıfır recomposition** ve sıfır boşta hücre animatörü ile sabit **120 FPS** çalışır.
- **TR:** `SquircleShape` süper-elips trigonometrisi önbelleğe alındı (`LRU Outline Cache`), `GlassSurface` fırçaları sabitlendi, tüm veri/arayüz modelleri `@Immutable` yapıldı ve Türkçe arama (`PreparedSearchQuery`) önden katlanmış alanlarla sıfır tahsisatlı hale getirildi.
- **TR:** Açılışta uygulama listesi ikon yüklemesini beklemeden anında ekrana gelir (`512` kapasiteli LRU önbellek + tekrarlı yükleme birleştirme + `45 sn` `onResume` tarama koruması), `LauncherActivity` izin kontrolleri arka plana taşındı ve uygulama başlatma animasyonunun takılmaması için sayaç yazımı `450 ms` ertelendi.

---

## [0.3.2-alpha] — 2026-11-13 (Bug Hunt & Stability Sweep)

### 🐛 Fixed / Düzeltilenler
- **EN:** Unified UI ↔ DataStore page/slot normalization (`normalizePages`, `pruneUninstalledAndNormalize`) with identity-verified drag sources (`expectedItemId`, `expectedAppKey`), eliminating shifted-slot drops and ghost uninstalled apps.
- **EN:** Added iOS-style **App-on-App Drag Folder Creation** (`HoverDropTarget.CreateFolderWithApp` + frosted squircle halo) and prevented Dock + Folder app duplication.
- **EN:** Introduced `TurkishSearchNormalizer` fixing Turkish `'I' / 'İ' / 'ı' / 'i'` search mismatches (`"InShot"` ↔ `"inshot"`, `"Instagram"` ↔ `"insta"`, `"İşCep"` ↔ `"iscep"`) across Spotlight, App Library, Settings, and `AppCategoryClassifier`.
- **EN:** Removed impure `viewModelScope.launch` side-effects from `LauncherViewModel.uiState` `combine`, added DataStore `ReplaceFileCorruptionHandler` + `IOException` recovery, coalesced `InstalledAppsRepository` scans, added `FLAG_ACTIVITY_NEW_TASK` fallback, and evicted updated/uninstalled icons from cache.
- **EN:** Wired `customization.hapticFeedbackEnabled` globally via `CompositionLocalProvider(LocalHapticFeedback)`, synced Flashlight state across Control Center & Notification Center via `CameraManager.TorchCallback`, fixed Today View orphan widgets / `key(widgetId)` / `%0` battery fallback, bound `BuildConfig.VERSION_NAME` in Settings with `Dispatchers.IO` + `ON_RESUME` snapshots, deduplicated `LazyColumn` notification keys, guarded multi-touch dual-drag, and grouped Kotlin/AndroidX Dependabot PRs.
- **TR:** UI ile DataStore arasındaki sayfa/slot senkronizasyonu (`normalizePages` + kimlik doğrulamalı sürükleme) birleştirildi; silinen uygulamaların hayalet ikonları ve 1 uygulamalı klasör kalıntıları otomatik temizlendi.
- **TR:** Bir uygulamayı başka bir uygulamanın üstüne sürükleyip bırakarak **yeni klasör oluşturma** (buzlu squircle önizleme halkası ile) eklendi; Dock ve Klasör kopyalanma hatası giderildi.
- **TR:** `TurkishSearchNormalizer` ile Türkçe/İngilizce `'I' / 'İ' / 'ı' / 'i'` arama hatası (`"InShot"`, `"Instagram"`, `"İşCep"`, `"7×7 Izgara"`) Spotlight, Uygulama Arşivi ve Ayarlar genelinde çözüldü; arama sonuçlarına uzun basma menüsü eklendi.
- **TR:** Haptic Touch kapatma ayarı tüm yüzeylere bağlandı, El Feneri durumu `TorchCallback` ile senkronize edildi, Today View widget/pil hataları ve HNM Ayarlar sürüm (`BuildConfig.VERSION_NAME`) / arka plan yenileme sorunları giderildi.

---

## [0.3.1-alpha] — 2026-11-12 (Hotfix)

### 🛠️ Fixed / Düzeltilenler
- **EN:** Fixed a critical Main-Thread infinite loop in `LauncherScreen` (`awaitEachGesture`) that caused a freeze and crash when lifting the finger after pulling down Control Center.
- **EN:** Hardened `HnmIosStatusBar` broadcast registration with `ContextCompat.registerReceiver(..., RECEIVER_NOT_EXPORTED)` and moved status/overlay Binder IPC reads to `Dispatchers.IO` with `IntOffset.VisibilityThreshold` slide springs.
- **TR:** Sağ üstten Denetim Merkezi indirilirken parmak kalktığı anda `LauncherScreen` `awaitEachGesture` bloğunda oluşan Main Thread sonsuz döngüsü (takılma ve çökme) giderildi.
- **TR:** Android 14/15/One UI 9 `BroadcastReceiver` `SecurityException` koruması eklendi; Denetim ve Bildirim Merkezi sistem sorguları `Dispatchers.IO`'ya taşındı.

---

## [0.3.0-alpha] — 2026-11-12

### ✨ Added / Eklenenler
- **EN:** Added calm **iOS Status Bar** (`HnmIosStatusBar`) hiding One UI's top status bar icons, plus root `PointerEventPass.Initial` pull-down gesture interception.
- **EN:** Added **HNM Notification & Summary Center** (`NotificationCenterOverlay`) on top-left pull-down with Cover Sheet clock/date, live battery/ringer capsules, inline Quick Reminders, Suggested Apps, and Flashlight/Camera buttons.
- **EN:** Added **120 FPS App & Folder Drag-and-Drop** (`LauncherDragDropController`) supporting grid reordering, cross-page edge paging, folder merging, **dragging apps into the Dock**, and **dragging apps out of the Dock**.
- **EN:** Added minimalist depth recession (`94.5%` GPU scale), subtle page scale/fade transitions, animated active page capsule, and phase-staggered organic Edit Mode jiggle.
- **TR:** One UI üst durum çubuğunu gizleyen sade **iOS Durum Çubuğu** ve `PointerEventPass.Initial` tabanlı güvenilir sağ üst / sol üst çekme hareketleri eklendi.
- **TR:** Sol üstten çekince One UI yerine açılan **HNM Bildirim & Özet Merkezi** eklendi.
- **TR:** **Dock'a uygulama sürükleyip ekleme** ve **Dock'tan ana ekrana uygulama çıkarma** dahil tam 120 FPS uygulama/klasör sürükle-bırak mekanizması getirildi.

---

## [0.2.0-alpha] — 2026-11-12

### ✨ Added / Eklenenler
- **EN:** Built-in One UI-independent **HNM Settings** (`HnmSettingsOverlay` & `DeviceSystemController`) covering Battery (`%`, health, `°C`, voltage), Display & 120 FPS live Brightness, Sound & Haptics, Storage, Apps, Connectivity mini-panels, and RAM/Device Info.
- **EN:** Initial **Control Center** (`ControlCenterOverlay`) with vertical touch Brightness & Volume sliders, Flashlight, Rotation Lock, Media player controls, and 2x2 Connectivity pod.
- **EN:** Minimalist **Customization Studio** supporting up to **`7x7`** Home Screen & Folder grids (`2x2`/`3x3` folder preview) and **Resizable Dock** (`75%–125%` scale, `3–7` capacity, show/hide toggle, glass opacity).
- **TR:** One UI 9 görmeden çalışan tam ekran **HNM Ayarlar**, **Denetim Merkezi** ve **`7x7`** Ana Ekran / Klasör ızgarası ile **boyutlandırılabilir Dock** stüdyosu eklendi.

---

## [0.1.2-alpha] — 2026-11-12

### ✨ Added & Fixed / Eklenenler ve Düzeltilenler
- **EN:** Added native **120 FPS (`120Hz`)** display mode locking (`HighRefreshRateHelper`), `RoleManager.ROLE_HOME` default Home Launcher flow, and removed hardware shadow inner-rectangle artifacts on translucent frosted-glass cards.
- **TR:** **120 FPS** yüksek yenileme hızı kilidi, varsayılan Ana Ekran (`ROLE_HOME`) akışı ve Today View cam kartlarındaki iç dikdörtgen gölge izi düzeltmesi yapıldı.

---

## [0.1.1-alpha] — 2026-11-12

### 🛡️ Security / Güvenlik
- **EN:** Introduced `sideload` vs `playstore` product flavors and custom `CN=HNMUI` release signing so sideloaded APKs install cleanly with zero **Google Play Protect** warnings.
- **TR:** Google Play Protect uyarısını tamamen ortadan kaldıran `sideload` / `playstore` mimarisi ve özel sürüm imzalama yapılandırması eklendi.

---

## [0.1.0-alpha] — 2026-11-12

### 🎉 Initial MVP / İlk Sürüm
- **EN:** Initial Jetpack Compose launcher with Today View, Squircle Home Grid, Frosted Glass Dock, Spotlight Search, and automatic Turkish App Library categorization.
- **TR:** Today View, Squircle Ana Ekran, Buzlu Cam Dock, Spotlight Arama ve otomatik Türkçe Uygulama Arşivi içeren ilk MVP sürümü.
