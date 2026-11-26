# User Core Integration Tests Implementation Plan (rollout step 2 of 7)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Cover every user-facing behaviour of the `user` module (register, login with push token registration, profile, logout) with layer 1 core integration tests that drive the real ViewModels over the real core, faking only HTTP and platform edges.

**Architecture:** A hand-built `UserCoreHarness` (test source set of `user`) wires the production classes — `AuthenticationRepositoryImpl`, `AuthPreferenceDataStore`, `AuthHeaderInterceptor`, `UserConverter`, `PushTokenRepositoryImpl`, an in-memory `VivDatabase` and Retrofit from `HttpClientFactory` — against `FakeBackend`, `RecordingNavigator` and a new `FakePushTokenProvider`. Tests assert only states, navigation, received HTTP requests and persisted session data.

**Tech Stack:** JUnit 4, Robolectric, AssertK, `test-utils` (`CoreTestRule`, `recordStates`, `FakeBackend`, `RecordingNavigator`), Room in memory.

**Spec:** `docs/superpowers/specs/2026-11-26-testing-strategy-design.md`

## Global Constraints

- Branch `chore/user-core-tests`, created from `docs/testing-strategy-rules` (PR #140, which adds `FakeResponse`/`ReceivedRequest` and AssertK). The PR targets `main` after #140 merges; rebase is not needed if #140 is squash-merged (merge `origin/main` instead).
- New tests use AssertK, never AssertJ or Mockito. Only `FakeResponse` / `ReceivedRequest` from `FakeBackend`; never import MockWebServer types.
- The graph is built by hand; no Dagger in tests.
- No `Thread.sleep`; wait with `awaitCount` / `awaitValue` / `awaitRequest`.
- Test names `givenX_whenY_thenZ`; every test asserts.
- Existing mock-based tests (`LoginViewModelTest`, `RegisterViewModelTest`, `ProfileViewModelTest`, `AuthenticationRepositoryImplTest`) stay untouched.
- Bug policy: small, clear bug → fix in its own commit with the test asserting correct behaviour; large → issue + `// Pins current behaviour, see #NNN`.
- Before the PR: `./gradlew spotlessApply assembleDevelopmentDebug testDevelopmentDebugUnitTest` all pass.
- PR body lists covered behaviours (inventory "user" line), bugs fixed or pinned, and the Kover change for `user` and `push` (baseline on main: user 53.1%, push 0.0%).

## Review Focus

1. Login succeeds but the push token cannot be obtained (Firebase unavailable) → still logged in and on profile → Task 2 `givenPushTokenUnavailable_…`.
2. `/auth/` succeeds but `/user/` fails → no navigation, no user persisted, error shown → Task 2 `givenUserFetchFails_…`.
3. The `Authorization` header carries the token just received on the `/user/` and `/token/` calls (interceptor reads the store written moments before) → Task 2 happy-path request assertions.
4. Logout when invalidating the Firebase token fails → session kept, error state, no navigation → Task 4 `givenTokenInvalidationFails_…`.
5. Malformed JSON from `/auth/` → error state, not a crash → Task 2 `givenMalformedAuthResponse_…`.

---

### Task 1: `FakePushTokenProvider` and user fixtures in `test-utils`

**Files:**
- Modify: `test-utils/build.gradle` (`implementation project(':push')`)
- Create: `test-utils/src/main/java/cat/xlagunas/test/utils/FakePushTokenProvider.kt`
- Create fixtures in `test-utils/src/main/resources/fixtures/user/`: `auth_token.json` (`{"token":"token-123"}`), `user_anna.json` (`{"id":3,"username":"anna","firstname":"Anna","lastname":"Puig","email":"anna@example.com","profilePic":"https://example.com/anna.png"}`)
- Test: `test-utils/src/test/java/cat/xlagunas/test/utils/FakePushTokenProviderTest.kt`

**Interfaces:**
- Produces: `class FakePushTokenProvider(var token: String = "push-token-1") : PushTokenProvider` with `var failure: Throwable? = null` (when set, `getPushToken()` and `invalidatePushToken()` throw it), `val invalidations: Int`, and real `isTokenRegistered()` / `markTokenAsRegistered()` state (invalidation resets it).

- [ ] **Step 1: Write the failing tests:** `givenToken_whenGetPushToken_thenReturnsIt`; `givenFailure_whenGetPushToken_thenThrows`; `givenRegistered_whenInvalidated_thenNotRegisteredAndCounted` (`invalidations == 1`, `isTokenRegistered()` false).
- [ ] **Step 2: Run** `./gradlew :test-utils:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.test.utils.FakePushTokenProviderTest'` — expected FAIL: unresolved `FakePushTokenProvider`.
- [ ] **Step 3: Implement** the fake and add the fixtures.
- [ ] **Step 4: Run** the Step 2 command — expected PASS.
- [ ] **Step 5: Commit** `Add a fake push token provider and user fixtures to test-utils`.

### Task 2: `UserCoreHarness` and login behaviours

**Files:**
- Create: `user/src/test/java/cat/xlagunas/user/core/UserCoreHarness.kt`
- Test: `user/src/test/java/cat/xlagunas/user/core/LoginCoreTest.kt`
- Modify: `user/build.gradle` (`testImplementation libs.androidx.test.core` if missing)

**Interfaces:**
- Consumes: `HttpClientFactory.okHttpClient/retrofit`, `CoreTestRule.rxSchedulers`, `FakeBackend.url`, `RecordingNavigator`, `FakePushTokenProvider`.
- Produces: `class UserCoreHarness(backend: FakeBackend, rule: CoreTestRule)` with `val navigator: RecordingNavigator`, `val pushTokenProvider: FakePushTokenProvider`, `val dataStore: AuthDataStore`, `val database: VivDatabase` (in memory, `allowMainThreadQueries()`), `val sharedPreferences: SharedPreferences` (`"preferences"`, as in production), `fun loginViewModel(): LoginViewModel`, `fun registerViewModel(): RegisterViewModel`, `fun profileViewModel(): ProfileViewModel`, `fun givenLoggedInUser(id: Long = 3)` (inserts user + token + current id), `fun close()` (closes the database). OkHttp gets only `AuthHeaderInterceptor(dataStore)`.

Test class setup: `@RunWith(RobolectricTestRunner::class)`; rules `CoreTestRule` and `FakeBackend`; harness created in `@Before`, closed in `@After`. Routes: `POST /auth/`, `GET /user/`, `PUT /token/`.

- [ ] **Step 1: Write the failing tests:**
  - `givenValidCredentials_whenLogin_thenSuccessAndNavigatesToProfile`: auth `ok("user/auth_token.json")`, user `ok("user/user_anna.json")`, token `ok()`; `login("anna","secret")`; states `awaitCount(1) == [SuccessLoginState]`; `navigator.calls.awaitCount(1) == [ToProfile]`.
  - `givenValidCredentials_whenLogin_thenSendsCredentialsAndAuthorizedRequests`: requests in order `POST /auth/` with body `{"username":"anna","password":"secret"}`, `GET /user/` with `header("Authorization") == "token-123"`, `PUT /token/` with body `{"value":"push-token-1","platform":"ANDROID"}` and the same header.
  - `givenValidCredentials_whenLogin_thenSessionPersisted`: `dataStore.authToken() == "token-123"`, `dataStore.getCurrentUserId() == 3`, `database.userDao().loadAllByUserId(3)` has `username == "anna"`, `pushTokenProvider.isTokenRegistered()` true.
  - `givenUnauthorized_whenLogin_thenAuthenticationErrorAndNoSession`: `/auth/` `httpError(401)` → `[InvalidLoginState("Authentication error")]`, no navigation, `dataStore.isAuthTokenAvailable()` false, no `GET /user/` received.
  - `givenConflict_whenLogin_thenAlreadyRegisteredError`: 409 → `[InvalidLoginState("User already registered")]`.
  - `givenServerError_whenLogin_thenErrorWithHttpMessage`: 500 → one `InvalidLoginState`, no navigation.
  - `givenDisconnect_whenLogin_thenErrorAndNoNavigation`: `disconnect()` → one `InvalidLoginState`, no navigation.
  - `givenMalformedAuthResponse_whenLogin_thenErrorAndNoNavigation`: `malformedJson()` on `/auth/`.
  - `givenUserFetchFails_whenLogin_thenErrorAndNoUserPersisted`: `/user/` 500 → one `InvalidLoginState`, no navigation, `getCurrentUserId() == -1`, no user rows.
  - `givenPushRegistrationFails_whenLogin_thenStillLoggedIn`: `/token/` 500 → `[SuccessLoginState]`, `[ToProfile]`, `isTokenRegistered()` false.
  - `givenPushTokenUnavailable_whenLogin_thenStillLoggedInWithoutTokenRequest`: `pushTokenProvider.failure = IOException()` → `[SuccessLoginState]`, `[ToProfile]`, no `PUT /token/` received.
  - `givenLoginScreen_whenNavigateToRegistration_thenRegistrationShown`: `[ToRegistration]`.
  "No navigation" / "no request" assertions are made after the state has been awaited, so nothing is still in flight.
- [ ] **Step 2: Run** `./gradlew :user:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.user.core.LoginCoreTest'` — expected FAIL: unresolved `UserCoreHarness`.
- [ ] **Step 3: Implement** `UserCoreHarness`.
- [ ] **Step 4: Run** the Step 2 command — expected PASS. A failure here is either a harness bug (fix the harness) or a production bug (apply the bug policy; ledger it).
- [ ] **Step 5: Commit** `Add user core harness and login integration tests`.

### Task 3: Registration behaviours

**Files:**
- Test: `user/src/test/java/cat/xlagunas/user/core/RegisterCoreTest.kt`

- [ ] **Step 1: Write the failing tests** (route `PUT /user/`; user `User("anna","Anna","Puig","anna@example.com","https://example.com/anna.png","secret")`):
  - `givenNewUser_whenRegister_thenSuccessAndNavigatesToLogin`: `ok()` → `[Success]`, `[ToLogin]`.
  - `givenNewUser_whenRegister_thenSendsUserWithoutAuthorization`: body equals `{"username":"anna","firstname":"Anna","lastname":"Puig","email":"anna@example.com","profilePic":"https://example.com/anna.png","password":"secret"}` (Gson omits the null `id`), and `header("Authorization")` is null.
  - `givenExistingUsername_whenRegister_thenErrorAndNoNavigation`: 409 → one `RegistrationError` whose message contains `409`, no navigation.
  - `givenServerError_whenRegister_thenError`: 500 → one `RegistrationError` containing `500`.
  - `givenDisconnect_whenRegister_thenError`: one `RegistrationError`, no navigation.
- [ ] **Step 2: Run** `./gradlew :user:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.user.core.RegisterCoreTest'` — expected FAIL: class not compiled yet / then failing assertions until wired (the ViewModel exists, so after compiling the tests should pass; if they pass immediately, change one expected value to watch it fail, then restore — ledger it).
- [ ] **Step 3: Run** again — expected PASS.
- [ ] **Step 4: Commit** `Add registration integration tests`.

### Task 4: Profile and logout behaviours

**Files:**
- Test: `user/src/test/java/cat/xlagunas/user/core/ProfileCoreTest.kt`

- [ ] **Step 1: Write the tests** (all start with `harness.givenLoggedInUser()`):
  - `givenLoggedInUser_whenProfileOpened_thenShowsUser`: `recordStates(profileViewModel.user).awaitValue { it.username == "anna" }`.
  - `givenLoggedInUser_whenLogout_thenSessionClearedAndNavigatesToLogin`: states of `logoutState` contain `Idle` then `InProgress`; `[ToLogin]`; `dataStore.isAuthTokenAvailable()` false; `getCurrentUserId() == -1`; no user rows; `pushTokenProvider.invalidations == 1`; `backend.requests` empty.
  - `givenTokenInvalidationFails_whenLogout_thenErrorAndSessionKept`: `pushTokenProvider.failure = IOException()` → states `[Idle, InProgress, Error]`, no navigation, token and user row still present.
  - `givenLogoutError_whenErrorShown_thenIdle`: after the error, `onLogoutErrorShown()` → last state `Idle`.
- [ ] **Step 2: Run** `./gradlew :user:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.user.core.ProfileCoreTest'`; watch each new test fail once by inverting one expectation if it passes on first run (ledger it), then restore — expected PASS.
- [ ] **Step 3: Commit** `Add profile and logout integration tests`.

### Task 5: Verify, measure and open the PR

- [ ] **Step 1:** `./gradlew spotlessApply assembleDevelopmentDebug testDevelopmentDebugUnitTest :koverXmlReportCoverage` — all BUILD SUCCESSFUL.
- [ ] **Step 2:** Read line coverage for `user` and `push` from `user/build/reports/kover/reportCoverage.xml` and `push/build/reports/kover/reportCoverage.xml` (or the merged report) for the PR body.
- [ ] **Step 3:** Push and open the PR against `main` (after #140 is merged; until then against `docs/testing-strategy-rules`), title `Add core integration tests for the user module`, body per Global Constraints, `Refs #96`.
