# Testing strategy: a safety net for the Kotlin Multiplatform refactor

Date: 2026-11-26
Status: approved; revised 2026-11-26 to align the tooling with current KMP practice (see "Revision: KMP alignment")

## Intent

Viv is about to go through a massive refactor: the app's core (business logic, data,
and presentation logic) will move to a Kotlin Multiplatform shared module so it can be
reused by an iOS app. Later, the Android UI will move to Compose.

Before that starts, we need tests that tell us, with certainty, whether the refactor
changed the app's behaviour. The tests are the safety net; coverage numbers are a
by-product, not the goal.

The consequence that drives everything below: **tests must survive the refactor**.
A test that breaks whenever internals are rewritten (Retrofit → Ktor, Gson →
kotlinx.serialization, Room → Room KMP/SQLDelight, Dagger → something else, RxJava →
coroutines) tells us nothing about behaviour. So tests are written against boundaries
that will still exist after the refactor, and observe behaviour from the outside.

## Decisions already made

- **ViewModels will be shared** (androidx `lifecycle-viewmodel` in `commonMain`,
  exposing `StateFlow`). They are the outer boundary of the core, so they are the main
  test subject.
- **RxJava will disappear.** Nothing new may depend on Rx: no Rx-specific test
  helpers, fakes, or rules beyond a single replaceable adapter (see "Async control").
- **Compose comes after the tests are in place.** E2E flows must not depend on the UI
  toolkit (Maestro targets accessibility text and ids), and screenshot baselines
  (layer 3b) are recorded before the migration.
- **E2E tests run the real app against a fake backend**, deterministic and independent
  of the real backend. They are written with **Maestro** so the same journeys can run on
  the future iOS app (revision 2026-11-26; it replaces the original Espresso + robots plan).
- **Test code is written to be portable to `commonTest`** (revision 2026-11-26): AssertK
  assertions, Turbine for `Flow`/`StateFlow`, and only engine-agnostic types from
  `FakeBackend`.
- **Approach B** (below) was chosen.
- **Bugs found by the new tests** (see "Bug policy"): small, clear bugs are fixed in the
  same PR as the test that exposes them, in a separate commit; larger issues or missing
  features get their own GitHub issue, and the test pins today's behaviour with a
  comment linking to it.

## Goals

1. Every user-facing behaviour of the core is covered by tests that exercise the real
   core code end to end, with only the network and platform edges faked. The behaviours
   are listed in "Behaviour inventory".
2. A small set of E2E tests proves the assembled app works for the critical journeys.
3. Both layers run in GitHub Actions on every PR.
4. Tests written now can be moved to `commonTest` during the KMP refactor with
   mechanical changes only (swap the HTTP fake engine, swap the state collector).

## Non-goals

- Testing WebRTC media or the content of a conference call.
- Changing production behaviour, except fixing the bugs covered by the bug policy.
- Production refactors beyond the testability seams listed in "Production changes".
- The KMP migration, the coroutines migration (#95) and the Compose migration
  themselves.
- New features, for example refreshing the auth token on 401.
- Enforcing a coverage threshold (CLAUDE.md: no Kover `verify` rules without an issue).

## Anti-goals (this would be a failure even if tests pass)

- Tests that mock the classes between the ViewModel and the network (repositories,
  data sources, converters). They pin the current internals and break on refactor.
- New test infrastructure that only works with RxJava, LiveData, Retrofit or Dagger
  and must be rewritten, not adapted, during the refactor.
- Flaky tests that people learn to ignore. `Thread.sleep` is never allowed.
- Raising coverage with assertion-less or trivial tests (#89).

## Constraints

- The repository is public: GitHub-hosted runners are free; the cost of E2E in CI is
  time and flakiness, not money.
- `development` flavor `ENDPOINT` is a LAN IP; nothing may depend on reaching it.
- `conference` is a dynamic feature module; it is not part of the base APK used by
  instrumentation tests.
- `targetSdk` is 35 and there is no network security config, so cleartext HTTP is
  blocked by default.
- Room schemas v1 and v2 exist in `core/schemas/`, but there is no migration in code
  (see "Findings").
- Versions only in `gradle/libs.versions.toml`.
- Follow existing conventions: JUnit 4, `givenX_whenY_thenZ`, Spotless. New tests use AssertK.

## Approaches considered

**A. Outside-in, E2E first.** Espresso over the whole app with MockWebServer for every
flow, ViewModel tests later. Most refactor-proof, but slow, needs an emulator for all
coverage, costly to cover error cases, hard to diagnose, and the UI layer itself will be
rewritten for Compose.

**B. Core integration tests on the JVM + a thin E2E layer. (Chosen)** The bulk of the
tests drive a ViewModel with the *real* core underneath (repositories, cache,
converters, auth interceptor, Room in memory) and fake only HTTP (MockWebServer) and
platform edges. A handful of E2E journeys sit on top (originally Espresso + robots, now
Maestro flows; see "Revision: KMP alignment").
Fast, deterministic, cheap error coverage, runs in the existing CI job, and protects
exactly the part that moves to KMP. Cost: two layers to maintain; Robolectric stays
until Room goes KMP.

**C. Bottom-up unit tests per class with fakes.** Fastest coverage gain, but most
coupled to internals: it breaks the most during the refactor. Rejected because it
works against the reason for doing this.

## The layers

### Layer 1 — Core integration tests (JVM, the bulk)

Where: `src/test` of each feature module (`user`, `contact`, `call`, `push`) and `app`
for push routing. Run by `testDevelopmentDebugUnitTest`.

Subject: a ViewModel, or for push a `MessageProcessor`, the push router or the push
token flow, constructed with real collaborators all the way down to:

| Edge | Test double |
|---|---|
| HTTP backend | `FakeBackend` (MockWebServer inside, hidden), with real OkHttp + Retrofit + Gson built by `HttpClientFactory`. Tests only see `FakeResponse` and `ReceivedRequest` |
| Room | real `VivDatabase`, in memory (Robolectric), **one instance per test** shared by every collaborator |
| `SharedPreferences` / `AuthDataStore` | real implementation on Robolectric preferences, one instance per test |
| `Navigator` | `RecordingNavigator` fake that records calls |
| `PushTokenProvider` (Firebase), `TimeProvider`, notifications | small hand-written fakes |

Each test asserts on observable behaviour only:
- the sequence of UI states emitted;
- navigation calls recorded by the fake navigator;
- the HTTP requests the backend received (method, path, auth header, body);
- persisted state where it is part of the behaviour (e.g. contacts cached after a list,
  DB unchanged after a failed friend request, since those actions expose no UI state).

Each ViewModel action is covered for: success, 401/403, other 4xx, 5xx, network failure
(`SocketPolicy.DISCONNECT_AT_START`), and malformed response body, unless a case is
impossible for that endpoint.

**The graph is built by hand, not by Dagger.** A small per-feature harness (e.g.
`UserCoreHarness`) calls constructors directly. Dagger will likely be replaced in the
refactor; a hand-built graph survives it and keeps tests readable. Wiring of the real
Dagger graph is covered by layer 3.

Assertions use **AssertK** (multiplatform), not AssertJ, so tests move to `commonTest`
without rewriting assertions. Existing AssertJ tests stay as they are.

Mocks: new tests use fakes. Mockito is only acceptable for an Android platform edge that
will stay in `androidMain`; if a mock is ever unavoidable in code that moves to the shared
core, use Mokkery (supports Kotlin/Native), never Mockito or MockK.

### Layer 2 — Pure unit tests (JVM, where they make sense)

Kept for logic with no collaborators: converters, mappers, permission decisions
(`PermissionsOutcome`), error mapping. No mocks needed by definition. Not the focus; no
push to add them for coverage.

Existing mock-based ViewModel and repository tests stay. A layer 1 test does not
replace them automatically; an old test is deleted only when layer 1 covers all its
behaviours and it starts getting in the way of the refactor.

`signaling` keeps its current tests (already fake-based and Flow-based).

### Layer 3 — End-to-end tests (Maestro, emulator)

Where: `e2e/` at the repository root (Maestro YAML flows) plus a small fake backend.
Subject: the real app (real Dagger graph, Activities, Fragments, ViewModels), built in a
dedicated variant whose endpoint points to the fake backend.

Why Maestro (revision 2026-11-26): the refactor exists to ship an iOS app on the same core.
Maestro flows drive the app through the accessibility layer, so the same journeys can be
run against the iOS app later (with a per-platform `appId`). Espresso robots would have to
be rewritten for Compose anyway and never run on iOS.

Fake backend for E2E: Maestro runs outside the app process, so the backend cannot live in
the test process. A small JVM program (`e2e-backend`, run by Gradle on the CI host)
reuses the `test-utils` routing and JSON fixtures and listens on a fixed port; the emulator
reaches it at `10.0.2.2`. Each flow selects its scenario first through a control endpoint
(e.g. `POST /__scenario/login_ok`) using Maestro's HTTP scripting, so flows are
independent and order does not matter.

Build: an `e2e` build variant (or a Gradle property on `developmentDebug`) sets the endpoint
to the fake backend, allows cleartext only to that host, uses the placeholder
`google-services.json` and disables Crashlytics and Performance collection.

Critical journeys (initial set), matching what the app does today:
1. Register a new user → lands on the expected screen.
2. Login success → profile shown and push token registered (`PUT /token/`, checked through
   the fake backend's request log); login with wrong credentials → error shown.
3. Contact list (the start screen) loads from the backend, and is shown from the cache
   after a relaunch.
4. Search a contact and send a friend request.
5. Accept and reject a pending friend request.
6. Tap the call action on a contact → confirmation dialog → confirm → `PUT /call/` is sent
   (checked in the fake backend's request log). The conference module itself is not
   launched; the outgoing intent is not asserted at this layer.
7. An incoming "friend request" push updates the visible contact list. Pushes cannot be
   delivered through FCM in tests, so the `e2e` variant (only) registers a deep link,
   `viv-e2e://push?type=<MessageType>&params=<json>`, handled by a small activity that
   builds the push message and passes it to the push router, the same entry point
   `PushMessageHandler` uses. The flow triggers it with Maestro's `openLink`. Only FCM
   transport is left untested; the link never ships in `development` or `production`.

The existing `LoginFragmentTest`, `RegisterFragmentTest` and `ContactFragmentTest` (mock
ViewModel injected into a fragment) are deleted when these journeys land, together with
the mock-factory path in `TestApplication`.

Flows wait with Maestro's built-in visibility waits; no fixed sleeps.

### Layer 3b — Screenshot tests (Roborazzi, JVM)

Added in the 2026-11-26 revision as the safety net for the Views → Compose migration.
Each screen (login, register, profile, contact list, manage contacts, call confirmation) is
captured in its main states (loading, content, empty, error) with Roborazzi on Robolectric,
fed by the same fakes as layer 1. Baselines are committed. CI verifies them in the
`unit-tests` job. When a screen moves to Compose, its new screenshots are compared against
the View baselines and re-recorded only on purpose, in the same PR, with the diff images
attached.

## Shared test code

`test-utils` becomes the shared home for both JVM tests and instrumentation tests
(`testImplementation` and `androidTestImplementation`). It holds:
- `FakeBackend`: path-based routing with `respondTo(method, pathRegex, FakeResponse)`,
  `awaitRequest()` / `requests` returning `ReceivedRequest`. MockWebServer is an
  `implementation` detail: tests never import it, so the engine can become Ktor's
  `MockEngine` in `commonTest` without touching tests.
- AssertK, exposed as an `api` dependency.
- JSON fixtures for every endpoint (`/auth/`, `/user/`, `/contact/...`, `/call/`,
  `/token/`), success and error variants, shared by layers 1 and 3.
- `RecordingNavigator`, `FakePushTokenProvider` and other platform fakes.
- State collection and async control (below).

### Async control

The core is not synchronous even with trampoline schedulers: Retrofit uses
`RxJava2CallAdapterFactory.createAsync()`, so responses arrive on OkHttp threads, and
OkHttp talks to MockWebServer over a real socket. Tests therefore **wait for observable
outcomes with a bounded timeout** (e.g. `states.awaitCount(2)`, `backend.awaitRequest()`),
never for a fixed time.

One JUnit rule, `CoreTestRule`, owns the rest:
- sets `Dispatchers.Main` to a test dispatcher (coroutine code: `call`, `signaling`);
- builds an `RxSchedulers` with trampoline schedulers for code that schedules through
  it (`user`, `contact`, `push`, `app`);
- applies `InstantTaskExecutorRule` behaviour for `LiveData`;
- provides the `CoroutineScope` injected into `CallMessageProcessor` (see "Production
  changes").

When RxJava goes, the Rx line is deleted from the rule and no test changes.

### State collection

- ViewModels that expose `Flow`/`StateFlow`: use **Turbine** inside `runTest`
  (multiplatform, virtual time). This is the target for every ViewModel after the
  coroutines migration (#95).
- ViewModels that still expose `LiveData`: use `recordStates(liveData)` and its bounded
  `await...` helpers. `StateRecorder` blocks a JVM thread, so it is a transitional bridge:
  when a ViewModel moves to `StateFlow`, its tests move to Turbine.
- Tests never call `observeForever` or `collect` directly.

## Production changes

### Testability seams (no behaviour change)

The builder may add further seams **of the same kind**: changes that only make code
reachable or replaceable from tests and do not change behaviour. Each one goes in its
own commit.

1. Move the Retrofit/OkHttp construction out of `app`'s `NetworkModule` into a factory
   in `core` that takes the base URL and the interceptors as parameters. `NetworkModule`
   and the layer 1 harness both use it, so tests run the production configuration
   (converters, call adapter, `AuthHeaderInterceptor`) without Chucker.
2. Provide the base URL through Dagger instead of reading `BuildConfig.ENDPOINT` inside
   `provideRetrofit`, so the `e2e` build can point the real graph to `e2e-backend`.
3. Dropped in the KMP alignment revision: Maestro drives the real app process, so there is
   no in-process test application to swap modules into. The `e2e` variant provides a fake
   `PushTokenProvider` instead, and flows reset state with Maestro's `clearState`.
4. Extract the routing in `PushMessageHandler.onMessageReceived` (convert the message,
   dispatch to the matching `MessageProcessor`) into a plain injectable class.
   `PushMessageHandler` delegates to it; tests call it directly.
5. Inject a `CoroutineScope` into `CallMessageProcessor` instead of `GlobalScope`.
6. Add a network security config for the `e2e` variant only that allows cleartext to the
   `e2e-backend` host (`10.0.2.2`).

### Bug policy

When a test exposes a bug:
- **Small, clear bug:** the test asserts the correct behaviour and the fix goes in the
  same PR, in its own commit, with a sentence in the PR body.
- **Large change or missing feature:** open a GitHub issue; the test asserts today's
  behaviour with a comment `// Pins current behaviour, see #NNN`, and is updated when
  the issue is fixed.

Known bugs, classified as small (fixed with their tests):
- `VivDatabase` (`DatabaseModule.provideDatabase`) and `AuthDataStore`
  (`ApplicationModule.provideAuthDataStore`) are not `@Singleton`, so every injection
  gets a new instance with its own Room invalidation tracker / `BehaviorSubject`. A
  push that writes contacts likely does not refresh the list on screen. Fix: scope both
  as `@Singleton`.
- `ContactsMessageProcessor.processMessage` subscribes without an error handler: a
  network error while processing a push crashes the app.
- `MessageConverter` maps `ACCEPT_CALL` and `REJECT_CALL` to `TODO()`, so those pushes
  throw. Fix: treat them as unsupported (log and ignore) until the feature exists.
- FCM token rotation is broken: `PushMessageHandler.onNewToken` calls
  `clearPushToken()` without subscribing (no-op), and the new token is never sent to the
  backend, so pushes likely stop after a rotation until the next login. Expected
  behaviour: when logged in, register the new token with `PUT /token/` and keep the
  registered flag consistent; when logged out, do nothing until login.

Known issues to open as issues (pinned, not fixed here):
- The auth token is never refreshed: `AuthenticationRepository.refreshToken()`
  (`GET /refresh/`) has no caller, so an expired token ends in 401s. Refreshing on 401
  is a new feature.
- Missing Room migration v1 → v2 (#93, see "Findings").

## Behaviour inventory

Tracked per action × error category; this list, not a percentage, says when layer 1 is
done.

- **user:** register; login (including push token registration on success and what
  happens when it fails); load profile; logout (clears token, preferences, DB).
- **contact:** list (remote + cache); search; send friend request; accept; reject;
  create call from a contact.
- **call:** confirm call → create room → start conference; failure path.
- **push:** token registration at login; FCM token rotation; message routing by type;
  `ContactsMessageProcessor`; `CallMessageProcessor`; unknown/unsupported message types.

## CI

- Layers 1 and 2: unchanged, existing `unit-tests` job.
- Layer 3b: Roborazzi verification runs in the existing `unit-tests` job; diff images are
  uploaded as an artifact on failure.
- Layer 3: new `e2e` job in `.github/workflows/android.yml` on `ubuntu-latest` with KVM:
  starts `e2e-backend`, boots an emulator with an emulator runner action (AVD snapshot
  cached, animations disabled), installs the `e2e` build and runs the Maestro CLI over
  `e2e/`. Maestro reports and recordings are uploaded as an artifact. Runs on every PR in
  parallel with unit tests. **Not a required check at first**; becomes required once it
  has been stable for a few weeks. When the iOS app exists, the same flows run in a macOS
  job against the simulator.
- E2E code is not counted in Kover coverage (Kover stays JVM-only as today).

## Coverage tracking

No threshold. Progress is tracked by the behaviour inventory. The Kover PR comment
keeps showing the trend.

## Findings to act on

- **Missing Room migration.** `VivDatabase` is at version 2 and `core/schemas/` has
  `1.json` and `2.json`, but no `Migration(1, 2)` exists and no destructive fallback is
  configured: upgrading from a v1 install would crash. Covered by #93. A
  `MigrationTestHelper` test should land together with that migration; out of scope here.
- **Dead code:** `AuthenticationRepository.refreshToken()` and
  `PushTokenPresenter.registerToken()` have no callers.
- **Broken instrumentation runner config.** `call`, `contact`, `core` and `user` set
  `testInstrumentationRunner` to a non-existent class
  (`cat.xlagunas.viv.commons.cat.xlagunas.core.di.MockTestRunner`). Harmless today
  (no `androidTest` there) but should be fixed or removed.
- **Inconsistent test dependencies.** `conference` uses `kotlin-test-junit` with
  `junit` commented out; `contact` declares `androidTest` dependencies with no tests.
- **CLAUDE.md is outdated**: says `conference` has no tests and RxJava is used
  throughout, while `call`/`signaling` already use coroutines. Update the testing
  section with this strategy when the work lands.

## Rollout order

1. Foundations — done in #139 (`HttpClientFactory`, singleton fix, `ApplicationScope`,
   `test-utils`), plus the KMP alignment of `FakeBackend` and AssertK.
2. Layer 1 for `user` (register, login + push token registration, profile, logout).
3. Layer 1 for `contact` (list, cache, search, request, accept, reject, create call).
4. Layer 1 for `call` and push (seam 4; token rotation, routing, processors), with their
   bug fixes.
5. Layer 3b: Roborazzi screenshot baselines for every screen and state.
6. Layer 3 infrastructure: seams 2 and 6, `e2e` variant, `e2e-backend`, Maestro setup,
   `e2e` CI job (closes #88).
7. Layer 3 journeys; remove the old Espresso fragment tests.
8. Clean-up of findings.

Each step is its own PR against `main`. Steps 5–7 must land before the Compose migration
starts.

## Revision: KMP alignment (2026-11-26)

Compared with current Android and KMP practice (Now in Android's testing strategy, Android
test-doubles guidance, Room KMP testing guidance, KMP testing guides), the philosophy was
already aligned: fakes over mocks, real core with only edges faked, in-memory database,
most tests on the JVM. Four tooling choices would have forced rewrites during the refactor
and were changed:

| Before | After | Why |
|---|---|---|
| AssertJ | AssertK | AssertJ is JVM-only; AssertK runs in `commonTest`. |
| `recordStates` for everything | Turbine for `Flow`/`StateFlow`; `recordStates` only for `LiveData` | `StateRecorder` blocks a JVM thread; Turbine uses virtual time and is multiplatform. |
| `FakeBackend` exposing `MockResponse`/`RecordedRequest` | `FakeResponse`/`ReceivedRequest` | MockWebServer is JVM-only; KMP uses Ktor `MockEngine`. Tests stay unchanged when the engine changes. |
| Espresso + robots | Maestro flows + `e2e-backend` | Same journeys can validate the iOS app; Espresso would be rewritten for Compose anyway. |

Added: layer 3b screenshot tests with Roborazzi, as the safety net for the Compose
migration. Robolectric stays until Room KMP with `BundledSQLiteDriver` and DataStore
replace the Android-only storage.

## Left to the builder

- Exact names and package layout of harnesses, Maestro flows and fixtures.
- How the `e2e-backend` program and its scenario control endpoint are structured, provided
  it reuses the `test-utils` routes and fixtures.
- Emulator API level and runner action, provided the job is reproducible and cached.
- Whether a given legacy test is deleted once layer 1 covers it.
- Further testability seams of the kind described above.
