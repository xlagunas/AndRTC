package dagger

import cat.xlagunas.contact.di.ContactModule
import cat.xlagunas.contact.ui.ContactFragment
import cat.xlagunas.core.di.ApplicationComponent
import cat.xlagunas.push.PushModule
import cat.xlagunas.user.di.UserModule
import cat.xlagunas.viv.commons.di.ViewModelModule
import cat.xlagunas.viv.landing.MainActivity
import cat.xlagunas.viv.login.LoginFragment
import cat.xlagunas.viv.profile.ProfileFragment
import cat.xlagunas.viv.push.PushMessageHandler
import cat.xlagunas.viv.push.PushMessageProcessorsModule
import cat.xlagunas.viv.register.RegisterFragment

@Component(
    modules = [ViewModelModule::class, ContactModule::class, UserModule::class, PushModule::class, PushMessageProcessorsModule::class],
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