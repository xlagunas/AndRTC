package cat.xlagunas.viv.commons.di

import cat.xlagunas.viv.push.PushMessageTokenHandler
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilder {

    @ContributesAndroidInjector()
    abstract fun bindPushMessageService(): PushMessageTokenHandler
}