# 🎵 Lucid Player

<div align="center">

**AMOLED Dark • Glassmorphism • Auto-builds on every push**

[![CI/CD](https://github.com/YOUR_USERNAME/LucidPlayer/actions/workflows/build.yml/badge.svg)](https://github.com/YOUR_USERNAME/LucidPlayer/actions)
[![API 26+](https://img.shields.io/badge/API-26%2B-brightgreen)](https://android-arsenal.com/api?level=26)
[![Kotlin 2.0](https://img.shields.io/badge/Kotlin-2.0-blue)](https://kotlinlang.org)

</div>

---

## ⚡ Auto-Build: Push → APK in 4 minutes

> **Zero setup required.** Every push to `main` automatically:
> 1. Builds a signed APK (auto-generates keystore, no secrets needed)
> 2. Creates a GitHub Release with the APK attached
> 3. Available for download immediately

```
git push origin main
           ↓  (4 minutes)
GitHub Release → LucidPlayer-release.apk  ✅
```

---

## 🚀 Quick Start

```bash
# 1. Clone
git clone https://github.com/YOUR_USERNAME/LucidPlayer.git
cd LucidPlayer

# 2. Push to YOUR GitHub (creates auto-release)
git remote set-url origin https://github.com/YOUR_USERNAME/LucidPlayer.git
git push -u origin main

# 3. Watch it build → Actions tab → Download APK from Releases
```

That's it. **No secrets, no keystore setup, no tags needed.**

---

## 📲 Install APK

1. Go to your repo **Releases** tab
2. Download `app-release.apk`  
3. On Android: **Settings → Security → Install Unknown Apps → ON**
4. Open APK → Install

---

## ✨ Features

| Feature | Detail |
|---------|--------|
| 🎵 **Full Playback** | Media3 ExoPlayer, gapless, background |
| 🎨 **AMOLED UI** | Pure black + electric indigo glassmorphism |
| 💿 **Spinning Vinyl** | Animated record on Now Playing |
| ⏱️ **Sleep Timer** | 5 / 10 / 15 / 30 / 45 / 60 min |
| ⚡ **Speed Control** | 0.5× – 2.0× playback |
| 📋 **Queue** | Full playback queue view |
| ❤️ **Favourites** | One-tap save, dedicated section |
| 🔀 **Shuffle + Repeat** | Off / All / One |
| 🔍 **Live Search** | Real-time across songs, artists, albums |
| 🎤 **Artists** | Colour-coded gradient avatars |
| 💿 **Albums Grid** | 2-col artwork grid |
| 📱 **Notification** | Full media controls in notification bar |
| ▶️ **Mini Player** | Persistent bar with previous/play/next |

---

## 🏗️ Architecture

```
app/
├── di/             Hilt modules (ExoPlayer singleton)
├── data/
│   ├── models/     Song, Album, Artist, PlayerState
│   └── repository/ MusicRepository (MediaStore)
├── service/        MusicService (Media3 foreground)
├── viewmodel/      PlayerViewModel (sleep timer, speed, queue)
└── ui/
    ├── theme/      AMOLED colors, typography
    ├── components/ GlassCard, SongRow, MiniPlayer, PlayingBars
    └── screens/    Home, Library, Artists, Search, NowPlaying
```

**Stack:** Kotlin 2.0 · Jetpack Compose · Media3 · Hilt · Navigation · Coil · Room

---

## 🔐 Signing (Optional: Use Your Own Keystore)

By default, CI auto-generates a new keystore each build. For consistent signing:

1. **GitHub → Settings → Secrets → Actions** → Add:
   - `KEYSTORE_BASE64` — `base64 -i your.keystore | pbcopy`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
   - `STORE_PASSWORD`

2. Push → CI uses your keystore automatically.

---

## 🧪 Local Build

```bash
./gradlew assembleDebug          # Debug APK
./gradlew assembleRelease        # Release APK (uses debug keystore locally)
./gradlew installDebug           # Install on connected device
```

Requires: JDK 17 · Android SDK 35 · Android Studio Hedgehog+

---

<div align="center">Made with ♥ · Kotlin · Jetpack Compose · Media3</div>
