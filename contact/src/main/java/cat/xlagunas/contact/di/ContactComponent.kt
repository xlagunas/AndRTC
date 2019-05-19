package cat.xlagunas.contact.di

import cat.xlagunas.contact.ui.ContactFragment
import cat.xlagunas.core.di.ApplicationComponent
import dagger.Component

@Component(
    modules = [ContactModule::class],
    dependencies = [ApplicationComponent::class]
)
interface ContactComponent {
    @Component.Builder
    interface Builder {
        fun withParentComponent(component: ApplicationComponent): Builder
        fun build(): ContactComponent
    }

    fun inject(fragment: ContactFragment)
}