# 🧘 Yoga App — Android

A full-featured Android yoga app built in Kotlin with Firebase backend. Users can browse yoga classes, view instructor profiles, search by location, and book sessions in real time.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin |
| UI | Android XML layouts |
| Backend | Firebase Firestore |
| Auth | Firebase Authentication |
| Storage | Firebase Storage |
| Maps | Google Maps API |

## Features

- **Class scheduling** — browse and book yoga classes by date and type
- **Instructor profiles** — detailed bios, ratings, and class history
- **Location-based search** — find classes near you using Maps API
- **Real-time booking** — live seat availability and instant confirmation
- **Firebase auth** — email/Google sign-in
- **Push notifications** — booking reminders and class updates

## Project Structure

```
├── app/
│   ├── src/main/
│   │   ├── java/         # Kotlin source files
│   │   ├── res/          # Layouts, drawables, strings
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── gradle/
└── build.gradle
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 26+
- Firebase project with Firestore, Auth, and Storage enabled

### Setup

```bash
git clone https://github.com/eranCat/Yoga_app_android.git
```

1. Open in Android Studio
2. Add your `google-services.json` from Firebase console to `/app`
3. Add your Google Maps API key to `AndroidManifest.xml`
4. Build & run on emulator or device (API 26+)

### Environment

```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_MAPS_API_KEY" />
```

## Companion App

This app has an iOS companion: [Yoga-app-ios](https://github.com/eranCat/Yoga-app-ios)

## Author

**Eran Karaso** — [Portfolio](https://erancat.github.io/portfolio-site) · [GitHub](https://github.com/eranCat)
