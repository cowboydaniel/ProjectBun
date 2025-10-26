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
2. Allow the IDE to sync the project with its bundled toolchain (Android Gradle Plugin 8.13.0 and Gradle 9.x). No wrapper scripts are checked in, so Android Studio or a locally installed Gradle distribution must drive the build.
3. In the **Run/Debug Configurations** dialog, press **+ → Android App**. If the Module drop-down is empty, choose **Sync Project with Gradle Files** (toolbar icon) and reopen the dialog once the sync completes. Select the `:app` module and set the launch option to **Open Select Deployment Target Dialog** so you can pick a device each time. Save the configuration (for example, name it `Baby Development Tracker`).
4. Connect an Android device with USB debugging enabled or start an emulator running Android 7.0 (API 24) or higher, then select it from the deployment target dialog.
5. Use the new run configuration to build and run the **Baby Development Tracker** app.

## Tech Stack

- Kotlin & Jetpack Compose
- Material 3 components
- Gradle Kotlin DSL build scripts

## Testing

Instrumented and unit test targets are included via the default Android testing libraries. Execute them with:

```bash
gradle test
gradle connectedAndroidTest
```

> **Note:** Running instrumented tests requires an Android device or emulator.

## Family sharing and synchronization

Family linking is powered by the `FamilySyncGateway` abstraction that the UI depends on to create invites, register partners, and exchange journal updates.【F:app/src/main/java/com/example/babydevelopmenttracker/network/FamilySyncGateway.kt†L6-L49】 The concrete implementation that ships today is `PeerToPeerFamilySyncGateway`, which keeps an in-memory journal store per family and relays changes over the Google Nearby Connections API service identifier `com.example.babydevelopmenttracker.sync`.【F:app/src/main/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGateway.kt†L33-L107】【F:app/src/main/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGateway.kt†L484-L560】 There is no cloud endpoint; the host device acts as the remote authority, so disconnecting advertising or discovery pauses synchronization for everyone else.【F:app/src/main/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGateway.kt†L108-L179】【F:app/src/main/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGateway.kt†L560-L604】

### Current workflow

1. The expectant parent toggles **Share journal with partner** in Settings. The app generates or reuses a family secret, calls `createFamily`, and starts advertising over Nearby so partners can find the device.【F:app/src/main/java/com/example/babydevelopmenttracker/MainActivity.kt†L330-L360】【F:app/src/main/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGateway.kt†L64-L107】
2. A partner enters the invite code on their device. We invoke `registerFamilyMember`, persist the returned auth token, and kick off discovery to locate the host.【F:app/src/main/java/com/example/babydevelopmenttracker/MainActivity.kt†L281-L314】【F:app/src/main/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGateway.kt†L86-L140】
3. When either side saves or deletes a journal entry, `JournalRepository` mirrors the change through the gateway so every connected device receives updates (or replays them from the host if they reconnect later).【F:app/src/main/java/com/example/babydevelopmenttracker/data/journal/JournalRepository.kt†L16-L73】【F:app/src/main/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGateway.kt†L108-L179】

### Developer setup notes

- **Testing two-way sync:** Run the app on two emulators (or a device and emulator) signed into Google Play Services, complete onboarding on each, then use the Settings invite flow to link the family. The Settings screen exposes developer-only buttons to manually start/stop advertising, discovery, and endpoint connections so you can confirm Nearby state transitions without waiting for automatic retries.【F:app/src/main/java/com/example/babydevelopmenttracker/MainActivity.kt†L740-L767】
- **Disabling sync locally:** Turning off the **Share journal with partner** toggle tears down advertising, discovery, and any open Nearby endpoints, and clears the stored family link/secret so no further payloads are exchanged.【F:app/src/main/java/com/example/babydevelopmenttracker/MainActivity.kt†L360-L389】 Reinstalling the app resets the in-memory store maintained by `PeerToPeerFamilySyncGateway`.
- **Feature flags:** There are no build-time flags for family sync. Behaviour is entirely driven by user preferences (`partnerLinkApproved`, `shareJournalWithPartner`, stored link secret) persisted in `UserPreferencesRepository` and checked before every remote call.【F:app/src/main/java/com/example/babydevelopmenttracker/data/UserPreferencesRepository.kt†L120-L133】【F:app/src/main/java/com/example/babydevelopmenttracker/data/journal/JournalRepository.kt†L44-L73】

### Roadmap toward resilient peer-to-peer sync

The peer-to-peer gateway already batches registration and sync requests, but we plan to harden the flow before shipping it to production audiences. Next steps include persisting the local journal cache to disk for offline recovery, improving error surfacing in Settings, and adding instrumentation coverage to guard Nearby regressions.【F:app/src/main/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGateway.kt†L108-L179】【F:app/src/test/java/com/example/babydevelopmenttracker/network/PeerToPeerFamilySyncGatewayTest.kt†L9-L40】 Until those land, expectant parents will continue acting as the source of truth whenever sharing is enabled.
