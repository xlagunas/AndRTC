package cat.xlagunas.conference.di

import cat.xlagunas.conference.ui.ConferenceActivity
import cat.xlagunas.viv.dagger.ApplicationComponent
import dagger.BindsInstance
import dagger.Component
import cat.xlagunas.core.di.Feature

@Component(modules = [ConferenceModule::class], dependencies = [ApplicationComponent::class])
@Feature
interface ConferenceComponent {
    @Component.Builder
    interface Builder {
        fun parent(component: ApplicationComponent): Builder
        @BindsInstance
        fun roomId(roomId: String): Builder

        fun build(): ConferenceComponent

        @BindsInstance
        fun activity(activity: ConferenceActivity): Builder
    }

    fun inject(activity: ConferenceActivity)
}