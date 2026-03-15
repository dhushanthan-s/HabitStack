# HabitStack

A minimal, offline habit tracker with a github commit grid dashboard, colored habit tiles, and optional daily reminders.

## Features

- **Offline only** – All data stored locally (Room). No account or cloud sync.
- **Git commit grid style UI** – Dashboard with one card per habit; each card shows a 12×7 grid of days (completed = colored square, today = tappable to toggle).
- **Per-habit customization** – Name, description, color. Optional daily reminder (notification).
- **Permissions** – Only **notification** permission is used (for reminders). No network, storage, or other permissions.

## Build

- **Android Studio** – Open the project and run.
- **Command line** – Use **JDK 17** (Java 25 can cause build failures):
  ```bash
  export JAVA_HOME=/path/to/jdk17   # if needed
  ./gradlew assembleDebug
  ```
- **Min SDK** 24, **Target SDK** 35.

## Usage

1. Grant notification permission when prompted (needed only if you use reminders).
2. Tap **+** to add a habit (name, optional description, color, optional reminder time).
3. On the dashboard, tap a habit card to edit or delete.
4. Tap **today’s cell** (bottom-right in the grid) on a habit to mark it done or undo.

APK output: `app/build/outputs/apk/debug/app-debug.apk`.
