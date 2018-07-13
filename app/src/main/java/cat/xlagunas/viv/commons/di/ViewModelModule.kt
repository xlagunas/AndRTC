package cat.xlagunas.viv.commons.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import cat.xlagunas.viv.commons.ViewModelFactory
import cat.xlagunas.viv.contact.ContactViewModel
import cat.xlagunas.viv.contact.viewholder.FriendshipViewModel
import cat.xlagunas.viv.landing.MainViewModel
import cat.xlagunas.viv.login.LoginViewModel
import cat.xlagunas.viv.profile.ProfileViewModel
import cat.xlagunas.viv.push.PushTokenViewModel
import cat.xlagunas.viv.register.RegisterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun provideRegisterViewModel(registerViewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun provideLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun provideContactViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PushTokenViewModel::class)
    abstract fun providePushTokenViewModel(pushTokenViewModel: PushTokenViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FriendshipViewModel::class)
    abstract fun provideFriendshipViewModel(friendshipViewModel: FriendshipViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactViewModel::class)
    abstract fun provideContactsViewModel(contactViewModel: ContactViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}