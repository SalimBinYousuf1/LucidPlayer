# 🎵 Lucid Player

<div align="center">

![Lucid Player Banner](https://via.placeholder.com/800x200/000000/9D4EDD?text=🎵+LUCID+PLAYER)

**A world-class Android music player with AMOLED glassmorphism UI**

[![Build APK](https://github.com/YOUR_USERNAME/LucidPlayer/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/LucidPlayer/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg)](https://kotlinlang.org)

</div>

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎨 **AMOLED UI** | Pure black backgrounds with neon purple/blue glassmorphism |
| 🎵 **Full Playback** | Play, pause, skip, seek with Media3 ExoPlayer |
| 🔀 **Shuffle & Repeat** | Off / All / One repeat modes |
| ❤️ **Favorites** | Mark and filter your favorite tracks |
| 🔍 **Search** | Real-time search across songs, artists, albums |
| 📀 **Album Grid** | Beautiful 2-column album art grid |
| 🎤 **Artists** | Browse by artist with song/album counts |
| 📱 **Notification** | Media notification with playback controls |
| 💿 **Vinyl Animation** | Spinning vinyl record on now-playing screen |

---

## 📸 Screenshots

> The Now Playing screen features an animated vinyl record with glowing border effects, gradient progress bar, and glassmorphism action buttons.

---

## 🏗️ Architecture

```
LucidPlayer/
├── data/
│   ├── models/          # Song, Album, Artist, PlayerState
│   └── repository/      # MusicRepository (MediaStore queries)
├── service/
│   └── MusicService     # Media3 MediaSessionService
├── viewmodel/
│   └── PlayerViewModel  # Hilt ViewModel, controller bridge
└── ui/
    ├── theme/           # AMOLED colors, typography
    ├── screens/         # Home, Library, Artists, Search, NowPlaying
    └── components/      # MiniPlayer, reusable composables
```

**Stack:** Kotlin • Jetpack Compose • Media3 ExoPlayer • Hilt • Navigation Compose • Coil • Room

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35

### Build & Run

```bash
# Clone the repo
git clone https://github.com/YOUR_USERNAME/LucidPlayer.git
cd LucidPlayer

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Push to Your GitHub

```bash
# Initialize git (already done if cloned)
git init
git add .
git commit -m "feat: initial Lucid Player implementation"

# Create repo on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/LucidPlayer.git
git branch -M main
git push -u origin main
```

---

## 📦 GitHub Actions CI/CD

The workflow (`.github/workflows/ci.yml`) automatically:

| Trigger | Action |
|---------|--------|
| Push to `main`/`develop` | Lint + Tests + Debug APK |
| Pull Request | Lint + Tests |
| Tag `v*` (e.g. `v1.0.0`) | Full signed Release APK + GitHub Release |

### Setting Up Releases

1. Go to your GitHub repo → **Settings** → **Secrets and variables** → **Actions**
2. Add these secrets for signed releases:
   - `KEYSTORE_BASE64` — Base64 encoded keystore file
   - `KEY_ALIAS` — Your key alias
   - `KEY_PASSWORD` — Key password
   - `STORE_PASSWORD` — Keystore password

3. Create a release:
```bash
git tag v1.0.0
git push origin v1.0.0
```

The workflow will automatically build, sign, and publish the APK as a GitHub Release! 🎉

---

## 🎨 Design System

| Token | Value | Use |
|-------|-------|-----|
| `Void` | `#000000` | AMOLED background |
| `NeonPurple` | `#9D4EDD` | Primary accent |
| `CelestialBlue` | `#3D5AF1` | Secondary accent |
| `Aurora` | `#00D4FF` | Highlight / progress |
| `NeonPink` | `#FF2D78` | Favorites / danger |
| `GlassWhite` | `#14FFFFFF` | Glassmorphism overlay |

---

## 📋 Permissions

| Permission | Reason |
|-----------|--------|
| `READ_MEDIA_AUDIO` | Access music files (Android 13+) |
| `READ_EXTERNAL_STORAGE` | Access music files (Android 12 and below) |
| `FOREGROUND_SERVICE` | Background playback |
| `POST_NOTIFICATIONS` | Media notification (Android 13+) |

---

## 📄 License

```
MIT License — Copyright (c) 2026 Lucid Player
Permission is granted to use, copy, modify, and distribute freely.
```

---

<div align="center">
Made with ❤️ and Kotlin • <a href="https://github.com/YOUR_USERNAME/LucidPlayer">GitHub</a>
</div>
