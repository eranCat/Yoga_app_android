# Yoga App Android

A comprehensive Android application for yoga teachers and students to discover, schedule, and manage yoga classes and wellness events. Built with Kotlin and Firebase, featuring real-time updates, location-based search, and community engagement tools.

## 🧘 Features

- **Class Discovery** - Browse yoga classes by location, style, and instructor
- **Real-time Scheduling** - Sign up for classes with real-time availability updates
- **Location-based Search** - Find nearby yoga studios and instructors
- **Event Management** - Create and manage yoga events, workshops, and retreats
- **User Profiles** - Instructor and student profiles with ratings and reviews
- **Messaging System** - Direct messaging between instructors and students
- **Payment Integration** - Secure payment processing for class bookings
- **Calendar View** - Personal calendar with booked classes and reminders
- **Push Notifications** - Real-time updates about classes and bookings
- **Community Feed** - Share experiences and connect with other practitioners

## 🛠 Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM + Repository Pattern
- **UI Framework**: Android Material Design, Jetpack Compose (partial)
- **Database**: Firebase Firestore, Room (local cache)
- **Authentication**: Firebase Auth
- **Maps**: Google Maps API
- **Payments**: Stripe / PayPal SDK
- **Networking**: Retrofit, OkHttp
- **Real-time**: Firebase Realtime Database / Cloud Messaging
- **Build Tool**: Gradle

## 📁 Project Structure

```
app/
├── ui/
│   ├── screens/                # Compose / Activity screens
│   ├── fragments/              # Fragment-based UI
│   ├── adapter/                # RecyclerView adapters
│   └── theme/                  # Material Design themes
├── data/
│   ├── repository/             # Data repositories
│   ├── datasource/
│   │   ├── local/              # Room database
│   │   └── remote/             # Firebase services
│   └── model/                  # Data models / DTOs
├── domain/
│   ├── usecase/                # Business logic
│   └── repository/             # Repository interfaces
├── viewmodel/                  # MVVM ViewModels
├── di/                         # Dependency injection (Hilt)
├── utils/                      # Utility functions
└── notification/               # Firebase FCM handlers
```

## 🚀 Getting Started

### Prerequisites

- Android Studio 2021.1+
- Android SDK 21+ (minimum API level)
- Kotlin 1.8+
- Firebase account

### Installation

1. Clone the repository:
```bash
git clone https://github.com/eranCat/Yoga_app_android.git
cd Yoga_app_android
```

2. Set up Firebase:
   - Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Firestore, Authentication, Storage, and Cloud Messaging

3. Add API keys to `local.properties`:
```properties
GOOGLE_MAPS_API_KEY=your_google_maps_api_key
STRIPE_API_KEY=your_stripe_api_key
FIREBASE_WEB_API_KEY=your_firebase_web_api_key
```

4. Build and run:
   - Open the project in Android Studio
   - Select a device or emulator
   - Click "Run" or press `Shift + F10`

### Gradle Dependencies

Key dependencies are managed in `build.gradle`:
- Jetpack Libraries (Lifecycle, Navigation, Compose)
- Firebase SDK (Auth, Firestore, Storage, Messaging)
- Google Play Services (Maps, Location)
- Hilt for dependency injection
- Retrofit for API calls
- Glide for image loading

## 📱 User Flows

### For Students
1. **Browse Classes** - Search nearby yoga studios
2. **View Details** - See class schedule, instructor info, pricing
3. **Book Class** - Reserve spot and make payment
4. **Manage Schedule** - View bookings and cancel if needed
5. **Rate & Review** - Share experience with community
6. **Connect** - Message instructors with questions

### For Instructors
1. **Create Classes** - Set schedule, pricing, capacity
2. **Manage Bookings** - Approve registrations and manage attendance
3. **View Calendar** - Comprehensive schedule overview
4. **Communicate** - Message students about classes
5. **Track Revenue** - View earnings and payment history
6. **Gather Feedback** - Collect and respond to reviews

## 🗺️ Location Services

- **Google Maps Integration**: Display studios and routes
- **Geolocation**: Find classes within user-specified radius
- **Route Navigation**: Integrate with Google Maps for directions
- **Location Permissions**: Proper handling of Android permissions

## 🔔 Push Notifications

Firebase Cloud Messaging (FCM) enables:
- Class reminders before scheduled time
- New booking notifications for instructors
- Event updates and announcements
- Direct messaging alerts

## 💳 Payment Integration

- **Stripe Integration**: Secure payment processing
- **Multiple Payment Methods**: Card, digital wallets
- **Transaction History**: Receipt and payment records
- **Refund Handling**: Automated refund processing

## 🔐 Security

- **Firebase Authentication**: Secure user registration and login
- **Firestore Security Rules**: Row-level access control
- **Data Encryption**: TLS for network communication
- **Payment Security**: PCI DSS compliance with Stripe
- **Permission Management**: Proper Android permission handling

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew testDebugUnitTest --tests com.yoga.app.YogaRepositoryTest
```

## 🚀 Build & Release

### Debug Build
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build
```bash
./gradlew assembleRelease
# Create/update keystore in local properties
# Output: app/build/outputs/apk/release/app-release.apk
```

### Deploy to Play Store
1. Build signed release APK/AAB
2. Go to Google Play Console
3. Create new release
4. Upload APK/AAB and fill release notes
5. Review and publish

## 📊 Performance Optimization

- **Image Caching**: Glide with custom cache policies
- **Database Indexing**: Firestore composite indexes
- **Lazy Loading**: RecyclerView with ViewPager2
- **Memory Optimization**: Proper lifecycle management
- **Network Optimization**: Request batching and caching

## 🎨 UI/UX Features

- **Material Design 3**: Modern, consistent UI
- **Dark Mode Support**: System theme integration
- **Responsive Layouts**: Works on phones and tablets
- **Smooth Animations**: Transition effects and micro-interactions
- **Accessibility**: WCAG 2.1 compliance

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/YogaFeature`
3. Commit changes: `git commit -m 'Add yoga feature'`
4. Push branch: `git push origin feature/YogaFeature`
5. Open Pull Request

## 📝 License

MIT License - See [LICENSE](./LICENSE) for details

## 👤 Author

**Eran Karaso** - Full-Stack Developer  
GitHub: [@eranCat](https://github.com/eranCat)

## 🔗 Resources

- [Android Developer Guide](https://developer.android.com)
- [Kotlin Language](https://kotlinlang.org)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Google Maps API](https://developers.google.com/maps)
- [Stripe Android SDK](https://stripe.com/docs/mobile/android)

## 📞 Support

Found a bug? Have a suggestion? Please open an [issue](https://github.com/eranCat/Yoga_app_android/issues) on GitHub.

## 🚀 Roadmap

- [ ] Video class streaming
- [ ] AI-powered class recommendations
- [ ] Offline class access with sync
- [ ] Advanced analytics dashboard
- [ ] Social features expansion
- [ ] Wearable device integration