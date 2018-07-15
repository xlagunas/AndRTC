package cat.xlagunas.viv.commons.di

import cat.xlagunas.viv.landing.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun bindMainActivity(): MainActivity
}