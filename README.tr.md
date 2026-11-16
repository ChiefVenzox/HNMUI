<div align="center">

# HNMUI (Türkçe Dokümantasyon)

**iOS Sadeliği, Android Özgürlüğüyle Buluştu — Buzlu Cam Dokulu 120 FPS Ana Ekran**

[![Son Sürüm](https://img.shields.io/badge/s%C3%BCr%C3%BCm-v0.3.5--alpha-0A84FF?style=for-the-badge)](releases/HNMUI-latest.apk)
[![CI & Güvenlik](https://img.shields.io/github/actions/workflow/status/ChiefVenzox/HNMUI/android-ci.yml?branch=main&style=for-the-badge&label=CI%20%26%20G%C3%BCvenlik)](https://github.com/ChiefVenzox/HNMUI/actions)
[![Gizlilik](https://img.shields.io/badge/Gizlilik-S%C4%B1f%C4%B1r%20%C4%B0nternet-10B981?style=for-the-badge)](SECURITY.md#türkçe)
[![Lisans: MIT](https://img.shields.io/badge/Lisans-MIT-F59E0B?style=for-the-badge)](LICENSE)

[**🇬🇧 English (`README.md`)**](README.md) · [**📚 Wiki**](docs/wiki/Home.md) · [**🔒 Güvenlik (`SECURITY.md`)**](SECURITY.md#türkçe) · [**📋 Sürüm Geçmişi**](CHANGELOG.md) · [**🤝 Katkı Rehberi**](CONTRIBUTING.md#türkçe) · [**⬇️ Doğrudan APK İndir (`v0.3.5`)**](releases/HNMUI-latest.apk)

</div>

---

> [!TIP]
> ### 📦 Doğrudan APK İndir
> - **🚀 En Güncel Sürüm (Tablet, Yatay Mod & Cam Temaları):** [**`releases/HNMUI-latest.apk`**](releases/HNMUI-latest.apk) *(3.0 MB, Play Protect Uyumlu)*
> - **🌐 GitHub Raw Doğrudan İndirme:** [**HNMUI-latest.apk İndir**](https://github.com/ChiefVenzox/HNMUI/raw/main/releases/HNMUI-latest.apk) veya [**HNMUI-v0.3.5-tablet.apk**](https://github.com/ChiefVenzox/HNMUI/raw/main/releases/HNMUI-v0.3.5-tablet.apk)
> - **📁 Depo İçi Konum:** [`releases/HNMUI-latest.apk`](releases/HNMUI-latest.apk)

---

## Genel Bakış

**HNMUI**, **HNMLabs** tarafından geliştirilen; iOS işletim sisteminin dinginliğini, sistem uyumlu **Açık ve Koyu Buzlu Cam (*Adaptive Frosted Glass*)** dokusunu ve yay fiziğine (*spring physics*) dayalı 120 FPS animasyonlarını Android'in açık sistem gücüyle birleştiren saf, hızlı ve pil dostu bir **Android Ana Ekran (Home Launcher)** uygulamasıdır.

## ✨ Öne Çıkan Özellikler

- **Tablet & Yatay Ekran Uyarlaması:** Cihaz çevrildiğinde otomatik yatay moda geçiş, 6x4 ferah ızgara, iPadOS tarzı yüzen ortalanmış cam Dock barı, 2 sütunlu Bugün Panosu dashboard'u ve Uygulama Arşivi'nde 3-4 sütunlu kategori dizilimi.
- **Sistem Uyumlu Açık & Koyu Cam Motoru (`Otomatik` / `Açık` / `Koyu`):** Android sisteminin Koyu/Açık modunu yeniden başlatma gerektirmeden canlı takip eder; **Sedefli Açık Cam** ile **Gece Koyu Cam** arasında `380 ms` profesyonel geçiş animasyonu sunar ve Denetim Merkezi / HNM Ayarlar içinden tek dokunuşla değiştirilebilir.
- **Gerçek 120 FPS & Minimalist Animasyon Fiziği:** `HighRefreshRateHelper` ile ekranın en yüksek yenileme hızını (`90Hz` / `120Hz` / `144Hz`) kilitler; üst katmanlar açıldığında arka plan derinlik çekilmesi (`%94.5`), sıfır recomposition'lı GPU sayfa geçişleri ve doğal jiggle animasyonları sunar.
- **iOS Durum Çubuğu + Kesintisiz Sağ Üst Denetim Merkezi & Sol Üst Bildirim Merkezi:** Ana ekranda One UI üst çubuğunu gizleyerek kendi sade iOS durum çubuğunu sunar; kamera deliği duyarlı `86dp+` çekme alanı ve çapraz başparmak kavisi desteğiyle sağ üstten / sağ kenardan çekince **Denetim Merkezi** her seferinde kesintisiz açılır, sol üstten çekince One UI yerine doğrudan **HNM Bildirim & Özet Merkezi** iner.
- **120 FPS Sürükle-Bırak (Izgara + Klasör + Dock'a Sürükle / Dock'tan Çıkar):** Uygulamaları sayfalar arasında sürükleyebilir, klasöre bırakabilir, **Dock'a sürükleyip ekleyebilir** ve **Dock'tan sürükleyip ana ekrana çıkarabilirsiniz**.
- **One UI'dan Bağımsız Dahili "HNM Ayarlar" & `7×7` Özelleştirme Stüdyosu:** Pil sağlığı/sıcaklığı, parlaklık, görünüm modu, ses, depolama, RAM ve `4×4`–`7×7` ızgara/klasör ile Dock boyutu (`%75–%125`) ayarlarını sunar.

## 📦 İndirme ve Kurulum

Play Protect uyumlu, optimize edilmiş ve imzalanmış en güncel APK dosyasını doğrudan indirebilirsiniz:
- **[Doğrudan Depodan İndir: `releases/HNMUI-latest.apk`](releases/HNMUI-latest.apk)**
- **[GitHub Raw Bağlantısı: HNMUI En Güncel APK İndir](https://github.com/ChiefVenzox/HNMUI/raw/main/releases/HNMUI-latest.apk)**
- **[Tablet & Yatay Mod Özel Sürüm APK: `HNMUI-v0.3.5-tablet.apk`](https://github.com/ChiefVenzox/HNMUI/raw/main/releases/HNMUI-v0.3.5-tablet.apk)**
- Kurulumdan sonra ana ekranın üstündeki **"Ayarla"** çubuğuna dokunarak varsayılan ana ekran uygulaması olarak **HNMUI**'yi seçebilirsiniz.

## 🏗️ Proje Yapısı ve Dağıtım Profilleri

| Dağıtım Profili (`Flavor`) | Amaç | İzinler |
| :--- | :--- | :--- |
| **`sideload`** *(varsayılan)* | Google Play Protect gelişmiş koruma uyumlu doğrudan APK kurulumu | `VIBRATE`, `EXPAND_STATUS_BAR`, `ACCESS_NETWORK_STATE`, `WRITE_SETTINGS` + `<queries>` |
| **`playstore`** | Canlı okunmamış bildirim sayacı destekli Google Play mağaza sürümü | Ek olarak `HnmNotificationListenerService` |

## 🛠️ Derleme ve Test

```bash
# Play Protect uyumlu Sideload Release APK derleme
./gradlew assembleSideloadRelease

# Play Store Debug APK derleme
./gradlew assemblePlaystoreDebug

# Birim testlerini çalıştırma
./gradlew testSideloadDebugUnitTest
```
