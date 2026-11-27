# E2E tests (Maestro)

Black-box flows that run the `e2e` flavor of the app against `e2e-backend`, a JVM fake backend.
App id: `cat.xlagunas.viv.e2e.dev`.

## Prerequisites

- JDK 17 and the Android SDK (`ANDROID_HOME`)
- [Maestro](https://maestro.mobile.dev) CLI (`maestro --version`)
- An emulator or device (`adb devices`)

## Run locally

```bash
# 1. Start the fake backend on 127.0.0.1:8484 (the app reaches it at http://10.0.2.2:8484)
./gradlew :e2e-backend:run                 # keep it running in its own terminal

# 2. Build and install the app (needs a booted emulator)
./gradlew :app:assembleE2eDebug
adb install -r app/build/outputs/apk/e2e/debug/app-e2e-debug.apk

# 3. Run the flows
maestro test e2e/
```

Each flow selects its own scenario (`scripts/select_scenario.js`) and runs `clearState`.
No fixed sleeps: use `assertVisible` / `extendedWaitUntil`.

## Backend control endpoints

```bash
curl -X POST localhost:8484/__scenario/logged_in_contacts   # select a scenario (resets routes and request log)
curl localhost:8484/__requests                              # requests received since the scenario was selected
curl localhost:8484/__health
```

Scenarios: `logged_in_contacts`, `empty`.

## Add a scenario

Add it to `e2e-backend/src/main/java/cat/xlagunas/e2e/backend/Scenarios.kt` (routes and
JSON fixtures), then run a flow against it with `maestro test -e SCENARIO=<name> e2e/` (flows default to `logged_in_contacts`;
`BACKEND_URL` can be overridden the same way). Note `smoke` expects `logged_in_contacts`, so other scenarios make it fail.
