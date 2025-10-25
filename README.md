# Baby Development Tracker

An Android application built with Jetpack Compose that helps expectant parents explore fetal development milestones week by week. Use the slider to choose a week of pregnancy (4–42) and view curated highlights for the baby, common experiences for parents, and helpful tips. Previous and upcoming weeks are summarized to provide context for your journey.

## Features

- 📅 **Weekly insights:** Detailed descriptions of baby's development from week 4 through week 42.
- 🤰 **Parent changes:** Surface common symptoms and body changes to expect each week.
- 💡 **Actionable tips:** Suggestions to stay comfortable, prepared, and informed.
- 🎚️ **Interactive slider:** Quickly move between weeks to compare changes over time.
- 🌗 **Material 3 styling:** Supports light and dark themes with dynamic color on Android 12+.

## Getting Started

1. Open the project in [Android Studio](https://developer.android.com/studio).
2. Sync Gradle when prompted to download dependencies.
3. Connect an Android device or start an emulator running Android 7.0 (API 24) or higher.
4. Build and run the **Baby Development Tracker** app.

## Tech Stack

- Kotlin & Jetpack Compose
- Material 3 components
- Gradle Kotlin DSL build scripts

## Testing

Instrumented and unit test targets are included via the default Android testing libraries. Execute them with:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

> **Note:** Running instrumented tests requires an Android device or emulator.
