# Testing strategy: a safety net for the Kotlin Multiplatform refactor

Date: 2026-11-26
Status: draft, pending approval

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
- **Compose comes after the tests are in place.** E2E tests must be easy to port from
  Espresso (Views) to Compose testing.
- **Espresso tests are end-to-end with a fake backend** (MockWebServer), deterministic
  and independent of the real backend.
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
- Follow existing conventions: JUnit 4, AssertJ, `givenX_whenY_thenZ`, Spotless.

## Approaches considered

**A. Outside-in, E2E first.** Espresso over the whole app with MockWebServer for every
flow, ViewModel tests later. Most refactor-proof, but slow, needs an emulator for all
coverage, costly to cover error cases, hard to diagnose, and the UI layer itself will be
rewritten for Compose.

**B. Core integration tests on the JVM + a thin E2E layer. (Chosen)** The bulk of the
tests drive a ViewModel with the *real* core underneath (repositories, cache,
converters, auth interceptor, Room in memory) and fake only HTTP (MockWebServer) and
platform edges. A handful of Espresso E2E journeys, written with robots, sit on top.
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
| HTTP backend | `MockWebServer`, with real OkHttp + Retrofit + Gson built by the same factory as production |
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

### Layer 2 — Pure unit tests (JVM, where they make sense)

Kept for logic with no collaborators: converters, mappers, permission decisions
(`PermissionsOutcome`), error mapping. No mocks needed by definition. Not the focus; no
push to add them for coverage.

Existing mock-based ViewModel and repository tests stay. A layer 1 test does not
replace them automatically; an old test is deleted only when layer 1 covers all its
behaviours and it starts getting in the way of the refactor.

`signaling` keeps its current tests (already fake-based and Flow-based).

### Layer 3 — End-to-end tests (Espresso, emulator)

Where: `app/src/androidTest`. Subject: the real app (real Dagger graph, real Activity,
Fragments and ViewModels) with only these swaps:
- backend base URL → MockWebServer running in the test process, over cleartext HTTP
  to localhost (allowed by a debug-only network security config);
- database → a single in-memory Room instance;
- `PushTokenProvider` → fake;
- push → injected by calling the extracted push router with a built `RemoteMessage`.

MockWebServer uses a **path-based `Dispatcher`**, not queued responses, so request
order cannot make a test flaky. Responses come from shared JSON fixtures (see
"Shared test code").

Tests are written as scenarios over **robots** (one per screen):

```kotlin
@Test
fun givenRegisteredUser_whenLogin_thenSeesProfile() {
    backend.respondTo(LOGIN, ok("auth/login_ok.json"))
    backend.respondTo(PUSH_TOKEN, ok())

    login { enterUsername("anna"); enterPassword("secret"); submit() }
    profile { assertShowsUsername("anna") }
}
```

Only robots know about Espresso, view ids and `R`. When a screen moves to Compose, its
robot is rewritten with Compose testing APIs and the scenarios stay unchanged.

Critical journeys (initial set), matching what the app does today:
1. Register a new user → lands on the expected screen.
2. Login success → profile shown and push token registered (`PUT /token/`); login
   with wrong credentials → error shown.
3. Contact list (the start screen) loads from the backend, and is shown from the cache
   after a relaunch.
4. Search a contact and send a friend request.
5. Accept and reject a pending friend request.
6. Tap the call action on a contact → confirmation dialog → confirm → `PUT /call/` is
   sent and an `ACTION_VIEW` intent to the conference URL with the returned `roomId` is
   fired, asserted with Espresso-Intents. The conference module itself is not launched.
7. An incoming "friend request" push updates the visible contact list.

The existing `LoginFragmentTest`, `RegisterFragmentTest` and `ContactFragmentTest` (mock
ViewModel injected into a fragment) are replaced by these journeys and the mock-factory
path in `TestApplication` is removed.

No sleeps: waiting is handled by Espresso idling resources covering OkHttp and the
app's background work.

Firebase: the E2E build always uses `.github/ci/google-services.placeholder.json` and
disables Crashlytics and Performance collection, so test runs never report to the real
Firebase project and fork/Dependabot PRs behave the same as internal ones. Startup with
the placeholder must be verified as the first task of the E2E infrastructure step.

## Shared test code

`test-utils` becomes the shared home for both JVM tests and instrumentation tests
(`testImplementation` and `androidTestImplementation`). It holds:
- `FakeBackend`: MockWebServer wrapper with a path-based dispatcher, `respondTo(...)`,
  and helpers to read received requests.
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

`recordStates(source)` returns a live list of emitted states, with overloads for
`LiveData` and `Flow`, plus the `await...` helpers. Tests never call `observeForever` or
`collect` directly. When a ViewModel moves from `LiveData` to `StateFlow`, its tests keep
using `recordStates`.

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
   `provideRetrofit`, so layer 3 can point the real graph to MockWebServer.
3. Give `VivApplication` an override point for building its component, so the E2E
   `TestApplication` can swap `DatabaseModule` (in-memory) and the `PushTokenProvider`.
4. Extract the routing in `PushMessageHandler.onMessageReceived` (convert the message,
   dispatch to the matching `MessageProcessor`) into a plain injectable class.
   `PushMessageHandler` delegates to it; tests call it directly.
5. Inject a `CoroutineScope` into `CallMessageProcessor` instead of `GlobalScope`.
6. Add a debug-only network security config that allows cleartext to `localhost` /
   `127.0.0.1`.

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
- Layer 3: new `e2e` job in `.github/workflows/android.yml` on `ubuntu-latest` with KVM,
  using an Android emulator runner action, AVD snapshot cached, animations disabled,
  placeholder `google-services.json`, test reports uploaded as an artifact. Runs on
  every PR in parallel with unit tests. **Not a required check at first**; becomes
  required once it has been stable for a few weeks.
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

1. Foundations: seams 1, 5 and the singleton fix; `test-utils` for JVM, `FakeBackend`,
   fixtures, `CoreTestRule`, `recordStates`, `RecordingNavigator`. Open the issues for
   the pinned problems.
2. Layer 1 for `user` (register, login + push token registration, profile, logout).
3. Layer 1 for `contact` (list, cache, search, request, accept, reject, create call).
4. Layer 1 for `call` and push (seam 4; token rotation, routing, processors), with their
   bug fixes.
5. Layer 3 infrastructure: seams 2, 3 and 6, E2E test component, robots, idling,
   Firebase placeholder check, `e2e` CI job.
6. Layer 3 journeys; remove the old fragment tests.
7. Clean-up of findings and CLAUDE.md update.

Each step is its own PR against `main`.

## Left to the builder

- Exact names and package layout of harnesses, robots and fixtures.
- How idling resources and the `await...` helpers are implemented, provided there are
  no sleeps and timeouts are bounded.
- Emulator API level and runner action, provided the job is reproducible and cached.
- Whether a given legacy test is deleted once layer 1 covers it.
- Further testability seams of the kind described above.
