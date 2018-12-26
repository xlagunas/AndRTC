package cat.xlagunas.conference.di

import cat.xlagunas.conference.domain.UserSessionIdentifier
import cat.xlagunas.conference.ui.ConferenceActivity
import cat.xlagunas.core.di.ApplicationComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [ConferenceModule::class], dependencies = [ApplicationComponent::class])
@Singleton
interface ConferenceComponent {
    @Component.Builder
    interface Builder {
        fun parent(component: ApplicationComponent): Builder
        @BindsInstance
        fun roomId(roomId: String): Builder

        fun build(): ConferenceComponent

        @BindsInstance
        fun activity(activity: ConferenceActivity): Builder

        @BindsInstance
        fun userSession(userSession: UserSessionIdentifier): Builder


    }

    fun inject(activity: ConferenceActivity)
}