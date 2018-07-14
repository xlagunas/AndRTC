package cat.xlagunas.viv.commons.di

import cat.xlagunas.viv.contact.ContactFragment
import cat.xlagunas.viv.login.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeContactFragment(): ContactFragment

//    @ContributesAndroidInjector
//    abstract fun contributeProfileFragment():


}