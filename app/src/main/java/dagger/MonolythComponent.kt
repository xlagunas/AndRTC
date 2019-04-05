package dagger

import cat.xlagunas.core.di.ApplicationComponent
import cat.xlagunas.viv.commons.di.ViewModelModule
import cat.xlagunas.viv.contact.ContactFragment
import cat.xlagunas.viv.contact.ContactModule
import cat.xlagunas.viv.landing.MainActivity
import cat.xlagunas.viv.login.LoginFragment
import cat.xlagunas.viv.login.LoginModule
import cat.xlagunas.viv.profile.ProfileFragment
import cat.xlagunas.viv.push.PushMessageHandler
import cat.xlagunas.viv.push.PushModule
import cat.xlagunas.viv.register.RegisterFragment
import cat.xlagunas.viv.register.RegisterModule

@Component(
    modules = [ViewModelModule::class, ContactModule::class, LoginModule::class, RegisterModule::class, PushModule::class],
    dependencies = [ApplicationComponent::class]
)
interface MonolythComponent {

    @Component.Builder
    interface Builder {
        fun withParentComponent(component: ApplicationComponent): Builder
        fun build(): MonolythComponent
    }

    fun inject(activity: MainActivity)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: RegisterFragment)
    fun inject(fragment: ContactFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: PushMessageHandler)
}