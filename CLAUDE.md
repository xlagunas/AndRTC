# CLAUDE.md

Viv is an Android WebRTC video-calling app: users register, manage a contact list and
call each other. It is a multi-module Kotlin project using Dagger 2, RxJava 2, Room,
Retrofit and Firebase (Cloud Messaging, Crashlytics, Performance).

## Modules

| Module | Purpose |
|---|---|
| `app` | Application module. Owns the single Dagger `ApplicationComponent`, `VivApplication`, `MainActivity`, `AndroidNavigator`, push handling. |
| `core` | Shared foundations: `DisposableViewModel`, `Result`, `RxSchedulers`, `Navigator` interface, ViewModel factory/keys, Room database (`VivDatabase`, DAOs, entities), `AuthDataStore`, `MessageProcessor`. Defines the `ENDPOINT` build config per flavor. |
| `user` | Login, registration, profile and authentication (token storage, auth header interceptor). |
| `contact` | Contact list and friend requests (remote + local data sources, cache). |
| `call` | Call creation/confirmation. Has its own `CallComponent` subcomponent. |
| `conference` | **Dynamic feature module** with the WebRTC conference screen. Its `ConferenceComponent` depends on `ApplicationComponent`. Only its permission logic has unit tests. |
| `push` | Firebase push token registration. |
| `signaling` | Socket.IO signaling client used by the conference. |
| `test-utils` | Shared test code for JVM and instrumentation tests: `CoreTestRule`, `recordStates`, `RecordingNavigator`, `FakeBackend` and JSON fixtures, `MainDispatcherRule`. Re-exports `test-utils-jvm`. |
| `test-utils-jvm` | Pure-JVM shared test code (`StateRecorder`, `FakeBackend`, `RouteTable`, fixtures), re-exported by `test-utils`. |
| `e2e-backend` | JVM fake backend (built on `test-utils-jvm` routing (`RouteTable`) and fixtures) that the Maestro E2E flows talk to. |

Feature modules follow `ui/` → `domain/` → `data/` packages (`domain` holds interfaces and
models, `data` holds DTOs, converters, APIs and repository implementations). Older modules
(`call`, `push`) are flat.

## Build and test

Product flavors: `development` (points to a LAN backend), `production` and the debug-only `e2e`
(wired to `e2e-backend`).
Build types: `debug` and `release`. Use the `developmentDebug` variant for local work.
The `test` tasks of pure-JVM modules are aliased as `testDevelopmentDebugUnitTest`, so the
commands below also cover them.

The `e2e` flavor is debug-only, lives in `app/src/e2e/` and never ships. A per-flavor
`VariantModule` (`app/src/standard/java` vs `app/src/e2e/java`) provides `@BaseUrl` and
`PushTokenProvider`, so the e2e build talks to `e2e-backend` without Firebase. Details in
`e2e/README.md`.

```bash
./gradlew assembleDevelopmentDebug               # build the app
./gradlew testDevelopmentDebugUnitTest           # all unit tests (what CI runs)
./gradlew :user:testDevelopmentDebugUnitTest     # one module
./gradlew :user:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.user.login.LoginViewModelTest'
./gradlew spotlessApply                          # format (ktlint); CI runs spotlessCheck
./gradlew :koverHtmlReportCoverage :koverXmlReportCoverage  # merged coverage report (Kover)
python3 -m unittest discover -s .github/scripts -p 'test_*.py'  # tests of the CI coverage summary script
./gradlew :app:assembleE2eDebug                  # build the E2E APK
./gradlew :e2e-backend:run                       # start the fake backend (127.0.0.1:8484)
maestro test e2e/                                # run the Maestro flows (emulator + backend up)
./gradlew :app:checkE2eIsolation :app:testE2eDebugUnitTest  # E2E code stays out of shipping variants
```

Before declaring work done, run `spotlessApply`, then `assembleDevelopmentDebug`,
`testDevelopmentDebugUnitTest` and `:app:checkE2eIsolation :app:testE2eDebugUnitTest` (the
latter because `assembleDevelopmentDebug` does not compile `src/e2e`). All must pass.

Coverage: Kover is applied from the root `build.gradle` to the modules in its
`coverageModules` list (`test-utils`, `test-utils-jvm` and `e2e-backend` are test infrastructure and not covered), using the `developmentDebug` variant.
The merged report lands in `build/reports/kover/htmlCoverage/index.html` and
`build/reports/kover/reportCoverage.xml`; each module also gets
`<module>/build/reports/kover/reportCoverage.xml`. Generated code (Dagger, Room `_Impl`,
`R`/`BuildConfig`, view binding, Glide) is excluded. Use the leading colon to run only the
merged report (`:koverXmlReportCoverage`); without it Gradle also runs every module's own
`koverXmlReportCoverage` task. A new production module must be added to
`coverageModules` and, in `.github/workflows/android.yml`, to the `--module` list of the
"Write coverage summary" step and to the per-module `koverXmlReportCoverage` tasks of the
"Generate coverage report" step.

Requirements and caveats:
- `app/google-services.json` is gitignored (public repository) but required to build:
  - CI writes it from the `GOOGLE_SERVICES` secret. PRs that don't get the secret (forks,
    Dependabot) build with `.github/ci/google-services.placeholder.json`; pushes and manual
    runs still fail without it.
  - Locally, keep a copy outside the repository and point the `viv.googleServicesJson`
    Gradle property to it in `~/.gradle/gradle.properties`:
    `viv.googleServicesJson=/Users/<you>/.config/andrtc/google-services.json`.
    `app/build.gradle` copies it into `app/` whenever it differs, so fresh clones and git
    worktrees build without extra steps. Edit the external copy, not `app/google-services.json`.
- The Android SDK is found through `ANDROID_HOME` (or `sdk.dir` in the gitignored
  `local.properties`). Export `ANDROID_HOME` so worktrees, which have no `local.properties`,
  find it too.
- Versioning reads `git describe --tags` and the commit count, so the checkout needs tags
  and full history.
- **Do not run `assembleRelease` / `bundleRelease` locally**: release signing needs the
  `PROD_KEYSTORE_PASSWORD`, `PROD_KEY_ALIAS` and `PROD_KEY_PASSWORD` env vars, which only
  exist in CI. To check R8 rules locally, run `./gradlew :app:minifyProductionReleaseWithR8`.
- `testReleaseUnitTest` is disabled in `app`.

## Architecture

**Dependency injection (Dagger 2, KSP).** There is one `@Singleton` `ApplicationComponent`
in `app` (`cat.xlagunas.viv.dagger`). Feature modules expose Dagger `@Module`s (`UserModule`,
`ContactModule`, `PushModule`, …) that are installed in that component, so `app` depends on
them with `api` (required by the `conference` dynamic feature). Some features use their
own component: `CallComponent` is a subcomponent, and `ConferenceComponent` takes
`ApplicationComponent` as a dependency.

**ViewModels.** ViewModels extend `DisposableViewModel` (adds every Rx subscription to
`disposable`, cleared in `onCleared`), get constructor-injected with `@Inject`, and are
bound in `app/.../dagger/ViewModelModule.kt` with `@Binds @IntoMap @ViewModelKey(...)`.
Fragments obtain them with the `Fragment.viewModel(Foo::class.java)` extension from
`core/common`. A new ViewModel is not usable until it is bound in `ViewModelModule`.

**State.** ViewModels expose `LiveData` of a sealed state type (e.g. `LoginState` with
`SuccessLoginState` / `InvalidLoginState`), or `Result.Success` / `Result.Error` from `core`.
Errors are logged with Timber and mapped to a user-facing state; never let a subscription
error go unhandled. Show transient messages with `Fragment.displayMessage(...)` (Snackbar).

**Async.** Coroutines and Flow are the standard for new code. Inject `CoroutineDispatchers`
(`io`, `main`, `default`) from `core` instead of using `Dispatchers.*` directly, so tests can
swap them. Existing RxJava 2 code keeps using the injected `RxSchedulers` (`io`,
`mainThread`, `computation`) instead of `Schedulers.*` until its module is migrated
(issue #95).

**Navigation.** Use the `Navigator` interface from `core`; `AndroidNavigator` in `app`
implements it. Feature modules never reference each other's screens directly.

**Push messages.** Incoming FCM messages are routed by `PushMessageHandler` to
`MessageProcessor` implementations registered in `PushMessageProcessorsModule`.

**Persistence.** Room lives in `core` (`VivDatabase`). Schemas are exported to
`core/schemas/` and must be committed. Bumping the database version requires a migration
(see issue #93); do not use destructive migrations.

## Testing strategy (applies to every PR)

The tests are the safety net for the Kotlin Multiplatform refactor, so they must survive
it. Full design: `docs/superpowers/specs/2026-11-26-testing-strategy-design.md`.
Every PR that adds or changes behaviour follows these rules:

- **Layer 1 — core integration tests (JVM, the default).** Drive a ViewModel (or a push
  `MessageProcessor`) with the *real* core underneath: repositories, data sources,
  converters, `AuthHeaderInterceptor`, Room in memory (Robolectric), and Retrofit built
  with `HttpClientFactory`. Fake only the edges: HTTP with `FakeBackend`, `Navigator` with
  `RecordingNavigator`, and Firebase, time and notifications with hand-written fakes.
  Build the graph by hand in a small harness, not with Dagger.
- **Assert only observable behaviour:** the sequence of states (`recordStates(...)`), the
  recorded navigation, the HTTP requests the backend received, and persisted data when
  that is the behaviour. Cover success, 401/403, other 4xx, 5xx, disconnect and malformed
  body for each action that hits the network. Use only `FakeResponse` / `ReceivedRequest`
  from `FakeBackend`; never import MockWebServer types in tests.
- **Write tests that can move to `commonTest`:** AssertK for assertions in new tests (not
  AssertJ), Turbine inside `runTest` for `Flow`/`StateFlow`, and `recordStates` only for
  ViewModels that still expose `LiveData`.
- **Never mock** repositories, data sources or converters in new tests. Prefer fakes in
  `test-utils`. Mockito only for an Android platform edge that stays in `androidMain`; if a
  mock is unavoidable in code that moves to the shared core, use Mokkery.
- **No new test code tied to RxJava, LiveData, Retrofit or Dagger** beyond the adapters in
  `test-utils` (`CoreTestRule`, `recordStates`), so tests move to `commonTest` mechanically.
- **Layer 2 — pure unit tests** only for logic with no collaborators (converters,
  mappers, decisions such as `PermissionsOutcome`).
- **Layer 3 — E2E (Maestro flows in `e2e/`):** the real app in the `e2e` variant against
  `e2e-backend`, so the same flows can later run on iOS. Add or update a flow when a PR
  changes a critical journey (register, login, contacts, friend requests, call, push).
- **Layer 3b — screenshots (Roborazzi, JVM), from the Compose migration on:** its first
  PR (step 0) records baselines of the View screens; after that, a PR that changes a
  screen updates its baselines on purpose and attaches the diff.
- **Bugs found by tests:** fix small, clear bugs in the same PR, in their own commit, with
  the test asserting the correct behaviour. For large problems or missing features, open
  an issue and pin today's behaviour with `// Pins current behaviour, see #NNN`.
- **Waiting:** no `Thread.sleep`; use the bounded `await...` helpers.
- **PR description:** list the behaviours covered (and the inventory items they tick
  from the spec), bugs fixed or pinned, and the coverage change from the Kover comment.

## Testing conventions

- JUnit 4. AssertK `assertThat` in new tests (AssertJ remains in older ones). Robolectric for
  tests needing Android classes or Room.
- Use `CoreTestRule` from `test-utils` (`testImplementation project(':test-utils')`): it sets
  `Dispatchers.Main`, trampoline `RxSchedulers`, the `LiveData` executor and a `testScope`
  for `@ApplicationScope`. `MainDispatcherRule` remains for existing tests. Use Turbine
  (`libs.turbine`) for `Flow`/`StateFlow` and `recordStates(...)` for `LiveData`.
- Test names follow `givenX_whenY_thenZ`.
- Kotlin classes are final. Annotate a class with `@OpenForTesting` (from `core`) to make
  it mockable in debug builds through the `kotlin-allopen` plugin, instead of adding `open`.
- Every test must assert something; no empty or assertion-less tests (issue #89).
- UI journeys are covered by the Maestro flows in `e2e/`. No Espresso tests remain (no
  mock-ViewModel UI tests); new UI coverage goes in Maestro flows.

## Code style

- Formatting is enforced by Spotless with ktlint (`./gradlew spotlessApply`). CI fails on
  `spotlessCheck`.
- Use view binding (`_binding` / `binding` pattern, cleared in `onDestroyView`); data
  binding is disabled in `app`.
- Keep code, comments, identifiers and commit messages in English.

## Git and CI workflow

- The default branch is `main`. Never commit directly to it: create a branch
  (`feature/…`, `fix/…`, `chore/…`, `docs/…`) and open a PR against `main`.
- Commit messages are short imperative sentences (`Add ViewModel unit tests and fix their
  error handling`). Reference the GitHub issue in the PR body (`Closes #82`).
- CI (`.github/workflows/android.yml`) runs `spotlessCheck`, `assembleDevelopmentDebug` and
  `testDevelopmentDebugUnitTest` on every PR to `main`; the `unit-tests` job also runs
  `:app:checkE2eIsolation` and `:app:testE2eDebugUnitTest`. GitHub Actions is the only CI.
- The `e2e` job (`E2E (Maestro)`) builds the e2e APK and `e2e-backend`, boots an emulator and
  runs the Maestro flows in `e2e/`; it uploads the `maestro-report` artifact. It is not a
  required check.
- Coverage in CI: the `unit-tests` job writes a coverage table (total and per module) to the
  job summary and uploads a `coverage-report` artifact (HTML, XML, summary) on successful
  runs. A separate `coverage-comment` job (only `pull-requests: write`, no checkout)
  creates or updates one PR comment marked `<!-- kover-coverage -->`, only for PRs from
  branches of this repository (fork PRs get no comment). The table is built by
  `.github/scripts/coverage_summary.py`.
- No minimum coverage is enforced yet; a changed-code gate is a possible follow-up. Do not
  add Kover `verify` rules without an issue.

## Gotchas

- `android.nonTransitiveRClass=false`: modules rely on transitive `R` classes. Do not
  flip it without refactoring the `R` imports.
- `app/build.gradle` excludes `com.google.protobuf:protobuf-lite` everywhere to avoid a
  Firebase Performance crash in instrumentation tests. Keep that exclusion.
- The `development` flavor `ENDPOINT` is a LAN IP (`core/build.gradle`); it is not reachable
  from CI or emulators outside that network.
- Dependency and plugin versions live only in the version catalog `gradle/libs.versions.toml`.
  Reference them as `libs.…` in `build.gradle` files; never hardcode a version in a module.
