package cat.xlagunas.viv.commons.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import cat.xlagunas.viv.commons.ViewModelFactory
import cat.xlagunas.viv.login.LoginViewModel
import cat.xlagunas.viv.register.RegisterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    abstract fun provideMainViewModel(registerViewModel: RegisterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun provideLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}