# Playlist Maker

<div align="center">

![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![API](https://img.shields.io/badge/API-29%2B-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

*A modern Android application for creating and managing your music playlists*

[Features](#features) • [Screenshots](#screenshots) • [Tech Stack](#tech-stack) • [Installation](#installation) • [Contributing](#contributing)

</div>

---

## About

Playlist Maker is an Android application for searching tracks, previewing audio, and managing a growing music experience. The project is being developed step by step using modern Android practices and Clean Architecture principles.

## Features

- **Track Search** — Search for music tracks via the iTunes Search API
- **Search History** — Quickly return to recently opened tracks
- **Audio Player** — Listen to 30-second track previews
- **Media Library** — Screen prepared for further playlist and library features
- **Dark Theme** — Toggle dark mode in settings
- **Share App** — Share the app link with others
- **Support** — Contact support directly from the app
- **User Agreement** — Open the agreement page from settings

## Screenshots

> TBD

## Tech Stack

- **Language:** Kotlin
- **Min SDK:** 29 (Android 10)
- **Target SDK:** 36
- **Architecture:** Clean Architecture (Data / Domain / Presentation)
- **Networking:** Retrofit + Gson Converter
- **Image Loading:** Glide
- **Storage:** SharedPreferences
- **Audio Playback:** MediaPlayer
- **UI:** Material Design Components
- **Build System:** Gradle (Kotlin DSL)

### Dependencies

- AndroidX Core KTX
- AndroidX AppCompat
- Material Components
- ConstraintLayout
- AndroidX Activity

## Installation

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 11 or higher
- Android SDK 29+

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/playlist-maker.git
   cd playlist-maker
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**
   - Connect your Android device or start an emulator
   - Click the "Run" button in Android Studio

## Project Structure

```
Playlist Maker/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/playlistmaker/
│   │   │   │   ├── data/           # Data layer (network, storage, repositories)
│   │   │   │   ├── domain/         # Domain layer (models, interactors, interfaces)
│   │   │   │   ├── presentation/   # UI layer (activities, adapters)
│   │   │   │   ├── App.kt
│   │   │   │   └── Creator.kt
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── ...
├── build.gradle.kts
└── README.md
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Author

- GitHub: [@sasha-zadiraka](https://github.com/sasha-zadiraka)
- LinkedIn: [Aleksandra Zadiraka](https://www.linkedin.com/in/aleksandra-zadiraka/)

---

<div align="center">

Made with love by Aleksandra Zadiraka

Star this repo if you find it helpful!

</div>
