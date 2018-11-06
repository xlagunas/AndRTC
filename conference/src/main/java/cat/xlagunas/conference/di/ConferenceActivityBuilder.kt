package cat.xlagunas.conference.di

import cat.xlagunas.conference.ui.ConferenceActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ConferenceActivityBuilder {

    @ContributesAndroidInjector(/*modules = [FragmentBuildersModule::class]*/)
    abstract fun bindConferenceActivity(): ConferenceActivity
}