# Yoga App - Android

Android application for yoga lessons, events, and community meetups with location-based discovery.

## Features

- **Class Discovery**: Find yoga classes and events near you
- **Community Meetups**: Discover local yoga groups and events
- **Real-time Booking**: Reserve spots in classes instantly
- **Event Calendar**: View and manage your yoga schedule
- **Teacher Profiles**: Browse instructors and their specialties
- **Ratings & Reviews**: Community feedback system
- **Social Features**: Connect with other yoga practitioners
- **Push Notifications**: Never miss a class or event

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Clean Architecture
- **UI**: Jetpack Compose / Material Design 3
- **Backend**: Firebase Realtime Database
- **Maps**: Google Maps API
- **Location**: Google Play Services
- **Networking**: Retrofit + OkHttp

## Project Structure

```
├── app/
│   ├── data/
│   │   ├── repository/      # Data repositories
│   │   └── remote/          # Firebase services
│   ├── domain/
│   │   ├── model/           # Domain models
│   │   └── usecase/         # Business logic
│   ├── presentation/
│   │   ├── ui/              # Compose screens
│   │   ├── viewmodel/       # MVVM ViewModels
│   │   └── navigation/      # Navigation graph
│   └── di/                  # Dependency injection
└── AndroidManifest.xml
```

## Getting Started

### Requirements

- Android Studio Hedgehog+
- Android SDK 24+
- Kotlin 1.9+

### Installation

```bash
# Clone and open in Android Studio
git clone https://github.com/eranCat/Yoga_app_android.git

# Sync gradle files
# File > Sync Now

# Create emulator or connect device
# Run the app
```

### Firebase Setup

1. Create Firebase project
2. Download `google-services.json`
3. Place in `app/` directory
4. Enable Realtime Database and Cloud Storage

## Features Explained

### Class Booking Flow
1. Search by location/style
2. View class details
3. Check availability
4. Complete booking
5. Receive confirmation

### Event Management
- Create/host yoga events
- Invite friends
- Track attendees
- Post event updates

### Social Features
- Follow other yogis
- Share achievements
- Comment on events
- Rate classes

## Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate coverage report
./gradlew jacocoTestReport
```

## Debugging

Enable Firebase emulator for local testing:

```kotlin
// In MainActivity
Firebase.database.useEmulator("10.0.2.2", 9000)
```

---

**Yoga connects us all** 🧘‍♂️