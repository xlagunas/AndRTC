# Testing Foundations Implementation Plan (rollout step 1 of 7)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Put in place the shared test infrastructure and the first production seams so that layer 1 core integration tests (steps 2–4) can be written against the real core with only HTTP and platform edges faked.

**Architecture:** Production gets three small changes: a shared Retrofit/OkHttp factory in `core`, `@Singleton` scoping for `VivDatabase` and `AuthDataStore`, and an injected application `CoroutineScope` in `CallMessageProcessor`. `test-utils` becomes usable from JVM tests and gains `StateRecorder`/`recordStates`, `RecordingNavigator`, `CoreTestRule` and `FakeBackend` (MockWebServer with a path-based dispatcher) plus a fixture loader.

**Tech Stack:** Kotlin, JUnit 4, AssertJ, OkHttp MockWebServer, Robolectric, kotlinx-coroutines-test, androidx arch core-testing, RxJava 2 (only inside `CoreTestRule`).

**Spec:** `docs/superpowers/specs/2026-11-26-testing-strategy-design.md`

## Global Constraints

- Work on branch `chore/testing-foundations` created from `main`; one PR against `main` with `Refs #96` in the body (the spec's issue list goes in the PR body).
- Versions only in `gradle/libs.versions.toml`; reference as `libs.…`.
- JUnit 4, AssertJ `assertThat`, test names `givenX_whenY_thenZ`, every test asserts something (#89).
- No `Thread.sleep` anywhere; every wait has a bounded timeout (default 5 seconds).
- Nothing new in `test-utils` may depend on RxJava except `CoreTestRule.rxSchedulers`.
- Seams must not change behaviour; the singleton change is the only behaviour change in this PR and goes in its own commit.
- Each task ends with `./gradlew spotlessApply` and a commit. Before the PR: `./gradlew spotlessApply assembleDevelopmentDebug testDevelopmentDebugUnitTest` must all pass.
- Coroutine migration issues #129–#134 are open and may land in parallel: helpers must support both `LiveData` and `Flow`, and RxJava and coroutines.

## Review Focus

1. A request the test did not stub must fail loudly with method and path in the message, never hang or return an empty 200 → Task 6 `givenUnstubbedRequest_…`.
2. An `await…` that times out must fail with the values recorded so far, so the failure is diagnosable → Task 4 `givenTooFewValues_whenAwaitCount_thenFailsListingRecordedValues`.
3. Values recorded from OkHttp threads concurrently with reads must not be lost or throw `ConcurrentModificationException` → Task 4 `givenConcurrentRecords_…`.
4. `CoreTestRule` must reset `Dispatchers.Main` even when the test body throws, or later tests in the same JVM break → Task 5 `givenFailingTest_…`.
5. Paths with a query string (`/contact/search/anna?x=1`) and with ids (`/contact/42/accept`) must match their route → Task 6 `givenQueryString_…` and `givenRegexRoute_…`.

---

### Task 1: Shared HTTP client factory in `core` (seam 1)

**Files:**
- Modify: `gradle/libs.versions.toml` (add `okhttp-mockwebserver = { module = "com.squareup.okhttp3:mockwebserver", version.ref = "okhttp" }`; if `4.12.1` does not resolve for mockwebserver, add a separate `mockwebserver` version entry with the closest 4.12.x)
- Create: `core/src/main/java/cat/xlagunas/core/network/HttpClientFactory.kt`
- Modify: `app/src/main/java/cat/xlagunas/viv/dagger/NetworkModule.kt`
- Modify: `core/build.gradle` (`testImplementation libs.okhttp.mockwebserver`, `libs.assertj.core`)
- Test: `core/src/test/java/cat/xlagunas/core/network/HttpClientFactoryTest.kt`

**Interfaces:**
- Produces:
  - `object HttpClientFactory`
  - `fun HttpClientFactory.okHttpClient(interceptors: List<Interceptor>): OkHttpClient` — adds the interceptors in list order, nothing else.
  - `fun HttpClientFactory.retrofit(baseUrl: String, client: OkHttpClient, gson: Gson): Retrofit` — `GsonConverterFactory.create(gson)`, `RxJava2CallAdapterFactory.createAsync()`, `baseUrl`, `client`. Exactly today's `NetworkModule` configuration.

- [ ] **Step 1: Write the failing tests** in `HttpClientFactoryTest` with a private test API `interface EchoApi { @GET("/echo/") fun echo(): Single<EchoDto> }`, `data class EchoDto(val value: String)` and a raw `MockWebServer`:
  - `givenJsonResponse_whenCallingThroughRetrofit_thenParsesWithGson`: server returns `{"value":"hi"}`; `api.echo().blockingGet()` equals `EchoDto("hi")`.
  - `givenInterceptors_whenRequest_thenAppliedInOrder`: two interceptors adding header `X-Order` values `1` then `2`; recorded request `headers.values("X-Order")` is `["1","2"]`.
  - `givenSuspendFunction_whenCalled_thenWorks`: an `@GET("/echo/") suspend fun echoSuspend(): EchoDto` returns `EchoDto("hi")` via `runBlocking` (guards the coroutine migration in `call`).
- [ ] **Step 2: Run** `./gradlew :core:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.core.network.HttpClientFactoryTest'` — expected FAIL: unresolved `HttpClientFactory`.
- [ ] **Step 3: Implement** `HttpClientFactory` as specified.
- [ ] **Step 4: Change `NetworkModule`** so `provideRetrofit` returns `HttpClientFactory.retrofit(BuildConfig.ENDPOINT, client, gson)` and `provideOkiHttpClient` returns `HttpClientFactory.okHttpClient(listOf(loggingInterceptor, ChuckerInterceptor.Builder(application).build(), authInterceptor))`, same order as today.
- [ ] **Step 5: Run** the test command from Step 2 and `./gradlew assembleDevelopmentDebug` — expected PASS / BUILD SUCCESSFUL.
- [ ] **Step 6: Commit** `Extract the Retrofit and OkHttp setup into a shared factory`.

### Task 2: Scope `VivDatabase` and `AuthDataStore` as singletons (bug fix)

**Files:**
- Modify: `app/src/main/java/cat/xlagunas/viv/dagger/DatabaseModule.kt` (`@Singleton` on `provideDatabase`)
- Modify: `app/src/main/java/cat/xlagunas/viv/dagger/ApplicationModule.kt` (`@Singleton` on `provideAuthDataStore`)
- Modify: `app/build.gradle` (`testImplementation libs.robolectric`; add `testOptions.unitTests.includeAndroidResources = true` only if Robolectric requires it)
- Test: `app/src/test/java/cat/xlagunas/viv/dagger/ApplicationComponentScopeTest.kt`

**Interfaces:**
- Consumes: `ApplicationComponent.vivDabase()`, `ApplicationComponent.authTokenDataStore()`.

- [ ] **Step 1: Write the failing tests** with `@RunWith(RobolectricTestRunner::class)` and `@Config(application = Application::class)` (avoid `VivApplication` and Firebase); component built with `DaggerApplicationComponent.builder().withApplication(ApplicationProvider.getApplicationContext()).build()`:
  - `givenComponent_whenDatabaseRequestedTwice_thenSameInstance`: `assertThat(c.vivDabase()).isSameAs(c.vivDabase())`.
  - `givenComponent_whenAuthDataStoreRequestedTwice_thenSameInstance`: same for `authTokenDataStore()`.
  - `givenDaoFromComponent_whenInsertingThroughAnotherInjection_thenObserverSeesIt`: `val subscriber = c.vivDabase().friendDao().getUserFriends(1L).test()`; insert `FriendEntity(friendId = 10, userId = 1, …)` through `c.friendDao().insert(...)` on a background thread (`Completable.fromAction { … }.subscribeOn(Schedulers.io()).blockingAwait()`, since the production database disallows main-thread queries); `subscriber.awaitCount(2)` and assert the last emitted list contains `friendId == 10L` (the user-visible effect of the bug: push writes not refreshing the list).
- [ ] **Step 2: Run** `./gradlew :app:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.viv.dagger.ApplicationComponentScopeTest'` — expected FAIL: `isSameAs` assertion error.
- [ ] **Step 3: Add the two `@Singleton` annotations.**
- [ ] **Step 4: Run** the Step 2 command — expected PASS.
- [ ] **Step 5: Commit** `Scope VivDatabase and AuthDataStore as singletons`; the message body says that unscoped providers gave every injection its own database and Room invalidation tracker.

### Task 3: Inject the application `CoroutineScope` into `CallMessageProcessor` (seam 5)

**Files:**
- Create: `core/src/main/java/cat/xlagunas/core/di/ApplicationScope.kt` (`@Qualifier @Retention(AnnotationRetention.RUNTIME) annotation class ApplicationScope`)
- Modify: `app/src/main/java/cat/xlagunas/viv/dagger/ApplicationModule.kt` (add `@Provides @Singleton @ApplicationScope fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)`)
- Modify: `call/src/main/java/cat/xlagunas/call/CallMessageProcessor.kt` (constructor gains `@ApplicationScope private val scope: CoroutineScope`; `GlobalScope.launch` → `scope.launch`)
- Modify: `call/build.gradle` (`testImplementation libs.robolectric`, `libs.androidx.test.core` if missing; `testOptions.unitTests.includeAndroidResources = true` for `R.string`)
- Test: `call/src/test/java/cat/xlagunas/call/CallMessageProcessorTest.kt`

**Interfaces:**
- Produces: `@ApplicationScope CoroutineScope` binding in the app graph; `CallMessageProcessor(context, notificationManager, channelId, gson, scope)`.

- [ ] **Step 1: Write the failing tests** (Robolectric; `TestScope(StandardTestDispatcher())`; message `CallMessage(MessageType.CREATE_CALL, """{"content":{"callId":"room-1"}}""")`; notifications read with `shadowOf(context.getSystemService(NotificationManager::class.java)).allNotifications`; create the channel `"test-channel"` in `@Before`):
  - `givenCallMessage_whenProcessedAndScopeNotAdvanced_thenNoNotificationYet`: `allNotifications` is empty.
  - `givenCallMessage_whenProcessed_thenNotificationWithAcceptAction`: after `scope.advanceUntilIdle()`, one notification whose single action title equals `context.getString(R.string.notification_action_btn)`.
- [ ] **Step 2: Run** `./gradlew :call:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.call.CallMessageProcessorTest'` — expected FAIL: constructor has no `scope` parameter.
- [ ] **Step 3: Implement** the qualifier, the provider and the constructor change.
- [ ] **Step 4: Run** the Step 2 command and `./gradlew assembleDevelopmentDebug` — expected PASS / BUILD SUCCESSFUL (Dagger resolves the new binding).
- [ ] **Step 5: Commit** `Inject an application CoroutineScope into CallMessageProcessor`.

### Task 4: `StateRecorder`, `recordStates` and `RecordingNavigator` in `test-utils`

**Files:**
- Modify: `test-utils/build.gradle`: `implementation project(':core')`; `api libs.junit`, `libs.assertj.core`, `libs.coroutines.test`, `libs.androidx.arch.core.testing`, `libs.okhttp.mockwebserver`; `implementation libs.androidx.lifecycle.livedata.ktx`, `libs.rxjava`; `testImplementation` same as needed.
- Create: `test-utils/src/main/java/cat/xlagunas/test/utils/StateRecorder.kt`
- Create: `test-utils/src/main/java/cat/xlagunas/test/utils/RecordStates.kt`
- Create: `test-utils/src/main/java/cat/xlagunas/test/utils/RecordingNavigator.kt`
- Test: `test-utils/src/test/java/cat/xlagunas/test/utils/StateRecorderTest.kt`, `RecordingNavigatorTest.kt`

**Interfaces:**
- Produces:
  - `class StateRecorder<T>` with `fun record(value: T)`, `val values: List<T>` (snapshot copy), `fun awaitCount(count: Int, timeout: Duration = 5.seconds): List<T>` (returns the first `count` values; on timeout throws `AssertionError` whose message contains `count`, the timeout and `values`), `fun awaitValue(timeout: Duration = 5.seconds, predicate: (T) -> Boolean): T`. Thread-safe; waiting uses a lock/condition, not polling with sleep.
  - `fun <T> recordStates(source: LiveData<T>): StateRecorder<T>` (registers `observeForever`; requires `CoreTestRule` or `InstantTaskExecutorRule`).
  - `fun <T> recordStates(source: Flow<T>, scope: CoroutineScope): StateRecorder<T>` (collects in `scope.launch(start = CoroutineStart.UNDISPATCHED)`).
  - `sealed interface NavigationCall` with `data class RequestCall(val userId: Long, val contactName: String)`, `data class StartCall(val roomId: String)`, `data object ToContacts`, `ToProfile`, `ToLogin`, `ToRegistration`.
  - `class RecordingNavigator : Navigator` with `val calls: StateRecorder<NavigationCall>`.

- [ ] **Step 1: Write the failing tests:**
  - `givenRecordedValues_whenAwaitCount_thenReturnsThemInOrder`.
  - `givenValuesFromAnotherThread_whenAwaitCount_thenReturnsOnceAvailable`: a thread records `1,2,3`; `awaitCount(3)` equals `[1,2,3]`.
  - `givenTooFewValues_whenAwaitCount_thenFailsListingRecordedValues`: record `"a"`, `awaitCount(2, 100.milliseconds)` throws `AssertionError` with message containing `[a]`.
  - `givenConcurrentRecords_whenReading_thenNoValueLost`: 4 threads × 1000 records while the test thread reads `values` in a loop; `awaitCount(4000)` has size 4000.
  - `givenLiveData_whenPostingValues_thenRecorded` (with `InstantTaskExecutorRule`): `MutableLiveData<String>` posted `"x"`, `"y"` → `values == ["x","y"]`.
  - `givenFlow_whenEmitting_thenRecorded`: `MutableSharedFlow<Int>` in `runTest`, `backgroundScope`, emit `1,2` → `values == [1,2]`.
  - `RecordingNavigatorTest.givenNavigatorCalls_whenRecorded_thenInOrder`: `navigateToProfile()`, `startCall("r1")` → `calls.values == [ToProfile, StartCall("r1")]`.
- [ ] **Step 2: Run** `./gradlew :test-utils:testDevelopmentDebugUnitTest` — expected FAIL: unresolved references.
- [ ] **Step 3: Implement** the three files as specified.
- [ ] **Step 4: Run** the Step 2 command — expected PASS.
- [ ] **Step 5: Commit** `Add state recording and a recording navigator to test-utils`.

### Task 5: `CoreTestRule`

**Files:**
- Create: `test-utils/src/main/java/cat/xlagunas/test/utils/CoreTestRule.kt`
- Test: `test-utils/src/test/java/cat/xlagunas/test/utils/CoreTestRuleTest.kt`

**Interfaces:**
- Produces: `class CoreTestRule(val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) : TestRule` with `val rxSchedulers: RxSchedulers` (all three `Schedulers.trampoline()`), `val testScope: TestScope` (on `testDispatcher`, for `@ApplicationScope`). Applies `InstantTaskExecutorRule` behaviour, `Dispatchers.setMain(testDispatcher)` before, `Dispatchers.resetMain()` after in a `finally`.

- [ ] **Step 1: Write the failing tests:**
  - `givenRule_whenRunning_thenMainDispatcherIsTestDispatcher`: launching on `Dispatchers.Main` runs on the rule's dispatcher (e.g. `withContext(Dispatchers.Main)` completes inside `runTest(rule.testDispatcher)` and a flag set there is true).
  - `givenRule_whenRunning_thenLiveDataSetValueWorksOffMainThread`: `MutableLiveData.setValue` from a background thread does not throw.
  - `givenFailingTest_whenRuleFinishes_thenMainDispatcherReset`: apply the rule manually to a `Statement` that throws; the original exception propagates, and afterwards `Dispatchers.Main` no longer delegates to the test dispatcher (`runBlocking { withContext(Dispatchers.Main) {} }` throws, since no Android main looper exists on the JVM).
  - `givenRule_thenRxSchedulersAreTrampoline`: `rxSchedulers.io`, `.mainThread`, `.computation` are `Schedulers.trampoline()`.
- [ ] **Step 2: Run** `./gradlew :test-utils:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.test.utils.CoreTestRuleTest'` — expected FAIL.
- [ ] **Step 3: Implement** `CoreTestRule`.
- [ ] **Step 4: Run** the Step 2 command — expected PASS.
- [ ] **Step 5: Commit** `Add CoreTestRule for dispatchers, schedulers and LiveData`.

### Task 6: `FakeBackend`, responses and fixtures

**Files:**
- Create: `test-utils/src/main/java/cat/xlagunas/test/utils/http/FakeBackend.kt`
- Create: `test-utils/src/main/java/cat/xlagunas/test/utils/http/FakeResponses.kt`
- Create: `test-utils/src/main/java/cat/xlagunas/test/utils/http/Fixtures.kt`
- Create: `test-utils/src/main/resources/fixtures/selftest/hello.json` (`{"value":"hello"}`)
- Test: `test-utils/src/test/java/cat/xlagunas/test/utils/http/FakeBackendTest.kt`

**Interfaces:**
- Produces:
  - `class FakeBackend : TestRule` (starts a `MockWebServer` before, shuts it down after). `val url: String` (server URL, no trailing slash issue: usable as Retrofit `baseUrl`). `fun respondTo(method: String, path: String, response: MockResponse)` where `path` is a regex fully matched against the request's encoded path **without** query; the latest registration for the same method+path wins. `fun awaitRequest(timeout: Duration = 5.seconds): RecordedRequest` (throws `AssertionError` on timeout). `val requests: List<RecordedRequest>`. Unstubbed request → response `404` with body `No fake response for <METHOD> <path>`, and the rule fails the test after the body with an `AssertionError` listing every unstubbed request.
  - `fun ok(fixture: String? = null): MockResponse` (200, `Content-Type: application/json`, body from `fixture(...)` or empty), `fun httpError(code: Int, body: String = ""): MockResponse`, `fun disconnect(): MockResponse` (`SocketPolicy.DISCONNECT_AT_START`), `fun malformedJson(): MockResponse` (200, body `{"broken"`).
  - `fun fixture(path: String): String` reads classpath resource `/fixtures/$path`, throws `IllegalArgumentException("Fixture not found: $path")` if missing.

- [ ] **Step 1: Write the failing tests** (use OkHttp directly against `backend.url`):
  - `givenStubbedRoute_whenRequested_thenReturnsFixture`: `respondTo("GET", "/selftest/", ok("selftest/hello.json"))` → body `{"value":"hello"}`, code 200.
  - `givenRegexRoute_whenRequestedWithId_thenMatches`: `respondTo("POST", "/contact/\\d+/accept", ok())`, POST `/contact/42/accept` → 200.
  - `givenQueryString_whenRequested_thenMatchesPathOnly`: stub `GET /contact/search/anna`, request `/contact/search/anna?x=1` → 200.
  - `givenUnstubbedRequest_whenRequested_thenNotFoundAndRuleFails`: run the rule manually around a statement that GETs `/nope/`; response 404 with body containing `GET /nope/`; the statement wrapped by the rule throws `AssertionError` mentioning `GET /nope/`.
  - `givenRequestMade_whenAwaitRequest_thenReturnsIt`: method and path recorded; `givenNoRequest_whenAwaitRequest_thenFailsAfterTimeout` with `100.milliseconds`.
  - `givenDisconnect_whenRequested_thenIOException`, `givenMissingFixture_whenLoading_thenFailsWithPath`.
- [ ] **Step 2: Run** `./gradlew :test-utils:testDevelopmentDebugUnitTest --tests 'cat.xlagunas.test.utils.http.*'` — expected FAIL.
- [ ] **Step 3: Implement** the three files (custom `Dispatcher` over a thread-safe route map).
- [ ] **Step 4: Run** the Step 2 command — expected PASS.
- [ ] **Step 5: Commit** `Add FakeBackend with path-based routing and JSON fixtures`.

### Task 7: Make `test-utils` available to feature tests, open the issue, verify and open the PR

**Files:**
- Modify: `user/build.gradle`, `contact/build.gradle`, `call/build.gradle`, `push/build.gradle`, `app/build.gradle`: `testImplementation project(':test-utils')`
- Modify: `contact/src/test/java/cat/xlagunas/contact/ui/ContactViewModelTest.kt` is **not** touched; existing tests stay as they are (spec, layer 2).

- [ ] **Step 1: Add the dependency** to the five modules and run `./gradlew testDevelopmentDebugUnitTest` — expected BUILD SUCCESSFUL with every existing test passing (no classpath conflicts from `api` deps of `test-utils`).
- [ ] **Step 2: Ask the user for permission, then open the GitHub issue** "Refresh the auth token on 401" (label `enhancement`): `AuthenticationRepository.refreshToken()` (`GET /refresh/`) has no caller, so an expired token ends in 401s; reference the spec. Record the issue number in the PR body. The missing migration is already #93.
- [ ] **Step 3: Final verification:** `./gradlew spotlessApply assembleDevelopmentDebug testDevelopmentDebugUnitTest` — all BUILD SUCCESSFUL; `./gradlew :koverXmlReportCoverage` succeeds.
- [ ] **Step 4: Commit** `Make test-utils available to JVM tests of feature modules`, push the branch and open the PR against `main` (title `Add testing foundations for core integration tests`; body: summary, the singleton bug fix called out, `Refs #96`, link to the spec).

---

## Next plans

Written after this PR is merged, against the real interfaces above:
2. Layer 1 for `user` — `docs/superpowers/plans/…-testing-user-core.md`
3. Layer 1 for `contact`
4. Layer 1 for `call` and push (seam 4, token rotation, processors and their bug fixes)
5. Layer 3 infrastructure (seams 2, 3, 6, E2E CI job; also closes #88)
6. Layer 3 journeys
7. Findings clean-up and CLAUDE.md update
