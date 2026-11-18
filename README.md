# Intermed Kiosk

Android app for the Telpo K8 self-service kiosk at Intermed Hospital. This first
cut only exercises the kiosk hardware; payments and the CRM back-end connection
come later.

## What is here
claude --resume 75bf3119-b547-484b-9c17-43b21c60e922

| Path | Purpose |
| --- | --- |
| `hardware/printer/` | Wrapper over Telpo's icod printer SDK (the K8's built-in ICOD PT80KM, on USB), status flags, receipt rendered to a 576 px bitmap |
| `hardware/scanner/` | Reader for the built-in scanner, which types codes as a USB keyboard and ends with ENTER |
| `hardware/usb/` | Lists attached USB devices (used to find the printer and, later, the POS terminal) |
| `ui/diagnostics/` | Test screen: connect the printer, read status, print a test receipt, show scans and USB devices |
| `app/libs/`, `app/src/main/jniLibs/` | Vendor binaries from Telpo (`icod_3.1.8l.jar` and its native libraries) |

Kotlin sources live under `app/src/main/kotlin/`.

## Build and run

1. Install Android Studio (it bundles the JDK and Android SDK).
2. Open this folder in Android Studio and let Gradle sync.
3. Enable USB debugging on the kiosk, connect it, and press Run.

Command line: `./gradlew :app:assembleDebug`, then
`adb install app/build/outputs/apk/debug/app-debug.apk`.

## Stack

Kotlin 2.4.21 (built into AGP), AGP 9.5.1, Gradle 9.9.1, Jetpack Compose +
Material 3, minSdk 28, targetSdk 34, compileSdk 37.
# intermed-kiosk
