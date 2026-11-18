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

You are continuing work on "intermed-kiosk", a Kotlin Android app for a Telpo K8D
self-service kiosk at Intermed Hospital (Mongolia). Repo:
https://github.com/Arvinbuy7n/intermed-kiosk (cloned here, branch main).

## What exists and is verified on the real kiosk
- Stack: Kotlin 2.4.21 (AGP built-in Kotlin), AGP 9.5.1, Gradle 9.9.1, Compose +
  Material 3, minSdk 28, targetSdk 34, compileSdk 37. Package mn.intermed.kiosk,
  sources under app/src/main/kotlin. Manual DI via AppContainer (no Hilt), no
  HTTP client, no navigation yet.
- hardware/printer: wrapper over Telpo's icod SDK (app/libs/icod_3.1.8l.jar +
  jniLibs). Printer is ICOD PT80KM on USB (VID 0483, PID 7540). It auto-connects
  on app start and prints receipts rendered as a 576 px bitmap. Works.
- hardware/scanner: Newland NLS-FM23 in USB keyboard mode, read through
  dispatchKeyEvent, ENTER-terminated. Works.
- hardware/usb: lists USB devices. ui/diagnostics: the single test screen (UI text
  in Mongolian). UsbSection is commented out in DiagnosticsScreen.kt on purpose.
- Kiosk: K8D, Rockchip RK3576, Android 14 (API 34), 1080x1920 portrait, arm64.
  It is reached with adb over Wi-Fi (Wireless debugging), not by cable.
- The kiosk's NFC is not used. Card payments go through a separate bank POS.

## The situation now
The bank's POS integration is a Windows exe (a local HTTP "bridge" between the
cash-register software and the POS terminal). It cannot run on the Android
kiosk. This Windows PC has that exe. Goal: test the POS, then let the kiosk app
drive it by calling the bridge on this PC over the LAN.

## Bridge protocols already used by the hospital's CRM web app (verify against the live exe)
- Golomt: GET http://localhost:8500/requestToPos/message?data=<base64 of JSON>.
  JSON: requestID (digits only), portNo ("15" in the CRM), timeout (ms, string),
  terminalID, amount (MNT x 100 as a string, e.g. 100₮ -> "10000"),
  currencyCode "496", operationCode ("1" sale, "26" logon/connection test,
  "59" settlement), bandWidth "115200", cMode "", cMode2 "", additionalData "",
  cardEntryMode "", fileData "". Response JSON has PosResult (a JSON string) ->
  { responseCode, responseDesc, data }, where data is base64 of UTF-8 JSON from
  the terminal. Approved = responseCode "00". terminalID is discovered with
  operationCode 26 and an empty terminalID.
- TDB: http://localhost:7000/api/ecr. GET = connection check. POST
  {"amount": <MNT>, "txnType": "sale"}; void = {"txnType": "CANCELLED"};
  settlement = {"txnType": "settlement"}. Response: success (boolean), authCode,
  responseCode, rrn, traceNo, message, amount, pan, terminalId.
- MBank: http://localhost:5050/api/ecr. POST /logon {"hostIndex":"01"} =
  connection check. POST /sale {"amount", "ecrRefNo" (uuid),
  "printMerchantReceipt", "printCustomerReceipt"}. POST /settlement. Response
  envelope: success, data { approved, code, description, maskedCardNumber,
  approvalCode, reference, amount, terminalId, ... } or error { code, message }.
- CRM timeouts: connection check 45 s, sale 120 s, cancel 10 s.

## What I want, in this order
1. Find out which bridge this PC runs: check which of ports 8500 / 7000 / 5050 is
   listening and whether it is bound to 127.0.0.1 or 0.0.0.0, and report this
   PC's LAN IP. Read-only commands only.
2. Run that bank's connection check (no money moves) and show me the raw response.
3. Give me a plan for the kiosk side and WAIT for my OK before writing any file.
   I expect roughly: hardware/pos/ (a client for that one bank's bridge, called
   over the LAN) and a POS section on the diagnostics screen with a PC address
   field, a "check connection" button and a small test sale.
4. After my OK: implement, build with gradlew, install on the kiosk, test.

## Things to account for in the plan
- If the bridge listens only on 127.0.0.1, the kiosk cannot reach it. Propose the
  fix (for example a netsh portproxy plus a firewall rule) and ask before
  changing anything on this PC.
- The app targets API 34, which blocks plain http:// by default. The plan must
  cover a network security config that allows cleartext to the bridge host only.
- Money is integer MNT everywhere. Never treat a timeout as a decline: the card
  may have been charged.
- Do not run a real sale, void or settlement until I explicitly say so, and tell
  me the amount first.

## How to work with me
- Plan first, then wait for an explicit OK. A comment from me is not approval.
- Additions only: do not rename, move or refactor existing files.
- Kotlin only. No explanatory comments in code. One composable per file.
  UI text in Mongolian (Cyrillic).
- Do not commit or push unless I ask. Never commit secrets or local.properties.
- I run installs and system settings changes myself unless I tell you to do it.
- Answer in English unless I write "mn", then answer in Mongolian (Cyrillic).
- Keep answers short and tell me plainly what was verified and what was not.
