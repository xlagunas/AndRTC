package cat.xlagunas.conference.di

import cat.xlagunas.viv.commons.di.ApplicationComponent
import cat.xlagunas.viv.commons.di.Feature
import dagger.Component

@Component(modules = [ConferenceActivityBuilder::class], dependencies = [ApplicationComponent::class])
@Feature
interface ConferenceComponent {

    @Component.Builder
    interface Builder {
        fun applicationComponent(component: ApplicationComponent)
        fun build(): ConferenceComponent
    }
}