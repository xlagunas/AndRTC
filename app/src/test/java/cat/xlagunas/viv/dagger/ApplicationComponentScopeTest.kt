package cat.xlagunas.viv.dagger

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import cat.xlagunas.core.persistence.db.FriendEntity
import cat.xlagunas.core.persistence.db.UserEntity
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class ApplicationComponentScopeTest {
    private lateinit var component: ApplicationComponent

    @Before
    fun setUp() {
        component =
            DaggerApplicationComponent.builder()
                .withApplication(ApplicationProvider.getApplicationContext())
                .build()
    }

    @Test
    fun givenComponent_whenDatabaseRequestedTwice_thenSameInstance() {
        assertThat(component.vivDabase()).isSameAs(component.vivDabase())
    }

    @Test
    fun givenComponent_whenAuthDataStoreRequestedTwice_thenSameInstance() {
        assertThat(component.authTokenDataStore()).isSameAs(component.authTokenDataStore())
    }

    @Test
    fun givenDaoFromComponent_whenInsertingThroughAnotherInjection_thenObserverSeesIt() {
        val subscriber = component.vivDabase().friendDao().getUserFriends(USER_ID).test()
        subscriber.awaitCount(1)

        Completable.fromAction {
            component.vivDabase().userDao().insert(user())
            component.friendDao().insert(friend())
        }
            .subscribeOn(Schedulers.io())
            .blockingAwait()

        subscriber.awaitCount(2)
        assertThat(subscriber.values().last().map { it.friendId }).contains(FRIEND_ID)
        subscriber.dispose()
    }

    private fun user() =
        UserEntity(
            id = USER_ID,
            username = "anna",
            firstName = "Anna",
            lastName = "Puig",
            email = "anna@example.com",
            imageUrl = null,
        )

    private fun friend() =
        FriendEntity(
            friendId = FRIEND_ID,
            userId = USER_ID,
            username = "bernat",
            name = "Bernat",
            email = "bernat@example.com",
            imageUrl = "",
            relationShipStatus = "ACCEPTED",
        )

    private companion object {
        const val USER_ID = 1L
        const val FRIEND_ID = 10L
    }
}
