package cat.xlagunas.viv.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.contact.ui.ContactViewModel
import cat.xlagunas.core.di.ViewModelFactory
import cat.xlagunas.core.di.ViewModelKey
import cat.xlagunas.user.login.LoginViewModel
import cat.xlagunas.user.profile.ProfileViewModel
import cat.xlagunas.user.register.RegisterViewModel
import cat.xlagunas.viv.home.MainViewModel
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
    @ViewModelKey(ContactViewModel::class)
    abstract fun provideContactsViewModel(contactViewModel: ContactViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun provideProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
