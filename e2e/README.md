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
Template for new flows, in this order: `clearState` (kills the previous app process), then the
`runScript` scenario selection (scenario hardcoded in the flow's `env`), then `launchApp`.
No fixed sleeps: use `assertVisible` / `extendedWaitUntil`.

## Backend control endpoints

```bash
curl -X POST localhost:8484/__scenario/logged_in_contacts   # select a scenario (resets routes and request log)
curl localhost:8484/__requests                              # requests received since the scenario was selected
curl localhost:8484/__health
```

Scenarios: `logged_in_contacts` (auth, contact list, search, accept/reject/request, call creation),
`contacts_empty` (logged in, no contacts), `register` (`PUT /user/` succeeds), `login_rejected`
(`POST /auth/` returns 401), `empty` (no routes).

## Assert a request reached the backend

`scripts/assert_request.js` fails the flow unless `GET /__requests` has an entry with the given
method and path (and, optionally, a body containing the `BODY_CONTAINS` substring, matched as plain text):

```yaml
- runScript:
    file: ../scripts/assert_request.js
    env:
      BACKEND_URL: ${BACKEND_URL || 'http://localhost:8484'}
      METHOD: POST
      PATH: /auth/
      BODY_CONTAINS: anna
```

## Add a scenario

Add it to `e2e-backend/src/main/java/cat/xlagunas/e2e/backend/Scenarios.kt` (routes and
JSON fixtures), then set it in the `SCENARIO` env of your flow's `runScript`. Each flow owns its scenario. To try a
scenario, select it with `curl -X POST localhost:8484/__scenario/<name>` or run a single flow file
(`maestro test e2e/flows/<flow>.yaml`). `BACKEND_URL` can be overridden with `maestro test -e BACKEND_URL=...`.
