# Playlist Maker

<div align="center">

![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![API](https://img.shields.io/badge/API-29%2B-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-purple.svg)
![Architecture](https://img.shields.io/badge/architecture-Clean%20Architecture-blue.svg)

*A modern Android application for searching, previewing, and managing music tracks*

[Features](#features) • [Screenshots](#screenshots) • [Tech Stack](#tech-stack) • [Project Structure](#project-structure)

</div>

---

## About

Playlist Maker is an Android application for searching music tracks via the iTunes Search API, listening to 30-second previews, and saving recently opened tracks to search history.
The project is developed using MVVM and Clean Architecture principles with a feature-based package structure.

---

## Features

- **Track Search** — Search for music tracks using the iTunes Search API
- **Search History** — Save and display recently opened tracks
- **Audio Player** — Listen to 30-second track previews
- **Dark Theme** — Enable or disable dark mode in settings
- **Share App** — Share the app link with other users
- **Support** — Contact support via email
- **User Agreement** — Open the user agreement page
- **Media Library** — Screen prepared for future media library and playlist features

---

## Screenshots

> TBD

---

## Tech Stack

- **Language:** Kotlin
- **Min SDK:** 29
- **Target SDK:** 36
- **Architecture:** MVVM + Clean Architecture
- **Package Structure:** Feature-based
- **Networking:** Retrofit + Gson Converter
- **Image Loading:** Glide
- **Local Storage:** SharedPreferences
- **Audio Playback:** MediaPlayer
- **UI:** XML layouts + Material Components
- **Build System:** Gradle Kotlin DSL

---

## Architecture

The project follows Clean Architecture principles and is split into three main layers inside each feature:

- **UI layer** — Activities, ViewModels, screen states, adapters
- **Domain layer** — Interactors, repository interfaces, business models
- **Data layer** — Repository implementations, Retrofit API, SharedPreferences, MediaPlayer, external Android intents

General dependency direction:

```text
UI → Domain → Data
```
The UI layer communicates with the business logic through ViewModels.
ViewModels expose immutable LiveData with screen state models and do not reference Activities directly.

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
