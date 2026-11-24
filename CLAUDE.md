# CLAUDE.md

Viv is an Android WebRTC video-calling app: users register, manage a contact list and
call each other. It is a multi-module Kotlin project using Dagger 2, RxJava 2, Room,
Retrofit and Firebase (Cloud Messaging, Crashlytics, Performance).

## Modules

| Module | Purpose |
|---|---|
| `app` | Application module. Owns the single Dagger `ApplicationComponent`, `VivApplication`, `MainActivity`, `AndroidNavigator`, push handling and all Espresso tests. |
| `core` | Shared foundations: `DisposableViewModel`, `Result`, `RxSchedulers`, `Navigator` interface, ViewModel factory/keys, Room database (`VivDatabase`, DAOs, entities), `AuthDataStore`, `MessageProcessor`. Defines the `ENDPOINT` build config per flavor. |
| `user` | Login, registration, profile and authentication (token storage, auth header interceptor). |
| `contact` | Contact list and friend requests (remote + local data sources, cache). |
| `call` | Call creation/confirmation. Has its own `CallComponent` subcomponent. |
| `conference` | **Dynamic feature module** with the WebRTC conference screen. Its `ConferenceComponent` depends on `ApplicationComponent`. Currently has no tests. |
| `push` | Firebase push token registration. |
| `signaling` | Socket.IO signaling client used by the conference. |
| `test-utils` | Helpers for instrumentation tests (`androidTestImplementation` only). |

Feature modules follow `ui/` → `domain/` → `data/` packages (`domain` holds interfaces and
models, `data` holds DTOs, converters, APIs and repository implementations). Older modules
(`call`, `push`) are flat.

## Build and test

Product flavors: `development` (points to a LAN backend) and `production`.
Build types: `debug` and `release`. Use the `developmentDebug` variant for local work.

```bash
./gradlew assembleDevelopmentDebug               # build the app
./gradlew testDevelopmentDebugUnitTest           # all unit tests (what CI runs)
./gradlew :user:testDevelopmentDebugUnitTest     # one module
./gradlew :user:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.user.login.LoginViewModelTest'
./gradlew spotlessApply                          # format (ktlint); CI runs spotlessCheck
./gradlew :koverHtmlReportCoverage :koverXmlReportCoverage  # merged coverage report (Kover)
python3 -m unittest discover -s .github/scripts -p 'test_*.py'  # tests of the CI coverage summary script
./gradlew connectedDevelopmentDebugAndroidTest   # Espresso, needs a device/emulator
```

Before declaring work done, run `spotlessApply`, then `assembleDevelopmentDebug` and
`testDevelopmentDebugUnitTest`. All three must pass.

Coverage: Kover is applied from the root `build.gradle` to the modules in its
`coverageModules` list (`test-utils` is not covered), using the `developmentDebug` variant.
The merged report lands in `build/reports/kover/htmlCoverage/index.html` and
`build/reports/kover/reportCoverage.xml`; each module also gets
`<module>/build/reports/kover/reportCoverage.xml`. Generated code (Dagger, Room `_Impl`,
`R`/`BuildConfig`, view binding, Glide) is excluded. Use the leading colon to run only the
merged report (`:koverXmlReportCoverage`); without it Gradle also runs every module's own
`koverXmlReportCoverage` task. A new module must be added to
`coverageModules` and, in `.github/workflows/android.yml`, to the `--module` list of the
"Write coverage summary" step and to the per-module `koverXmlReportCoverage` tasks of the
"Generate coverage report" step.

Requirements and caveats:
- `app/google-services.json` is gitignored (public repository) but required to build:
  - CI writes it from the `GOOGLE_SERVICES` secret.
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

**Async.** RxJava 2 throughout. Inject `RxSchedulers` (`io`, `mainThread`, `computation`)
instead of using `Schedulers.*` directly, so tests can swap them. A migration to coroutines
and Flow is planned (issue #95); until then, write new code with the existing RxJava
patterns, not with coroutines.

**Navigation.** Use the `Navigator` interface from `core`; `AndroidNavigator` in `app`
implements it. Feature modules never reference each other's screens directly.

**Push messages.** Incoming FCM messages are routed by `PushMessageHandler` to
`MessageProcessor` implementations registered in `PushMessageProcessorsModule`.

**Persistence.** Room lives in `core` (`VivDatabase`). Schemas are exported to
`core/schemas/` and must be committed. Bumping the database version requires a migration
(see issue #93); do not use destructive migrations.

## Testing conventions

- JUnit 4, Mockito (`mock(...)`, `` `when`(...) ``), AssertJ `assertThat`. Some modules
  use Robolectric for tests needing Android classes.
- Use `InstantTaskExecutorRule` for `LiveData`, and observe with `observeForever`,
  collecting emitted states into a list, then assert with `containsExactly(...)`.
- Test names follow `givenX_whenY_thenZ`.
- Kotlin classes are final. Annotate a class with `@OpenForTesting` (from `core`) to make
  it mockable in debug builds through the `kotlin-allopen` plugin, instead of adding `open`.
- Every test must assert something; no empty or assertion-less tests (issue #89).
- Espresso tests live only in `app/src/androidTest` and use `MockTestRunner` /
  `ApplicationTestComponent` to inject mocks. They are not run in CI yet (issue #88).

## Code style

- Formatting is enforced by Spotless with ktlint (`./gradlew spotlessApply`). CI fails on
  `spotlessCheck`.
- Use view binding (`_binding` / `binding` pattern, cleared in `onDestroyView`); data
  binding is disabled in `app`.
- Keep code, comments, identifiers and commit messages in English.

## Git and CI workflow

- The default branch is `kotlin`. Never commit directly to it: create a branch
  (`feature/…`, `fix/…`, `chore/…`, `docs/…`) and open a PR against `kotlin`.
- Commit messages are short imperative sentences (`Add ViewModel unit tests and fix their
  error handling`). Reference the GitHub issue in the PR body (`Closes #82`).
- CI (`.github/workflows/android.yml`) runs `spotlessCheck`, `assembleDevelopmentDebug` and
  `testDevelopmentDebugUnitTest` on every PR to `kotlin`. `.circleci/` is obsolete and
  scheduled for removal (issue #87); ignore it.
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
