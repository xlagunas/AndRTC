package cat.xlagunas.viv.commons.di

class VivApplicationTest : VivApplication() {

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationTestComponent
                .builder()
                .withApplication(this)
                .build()
        applicationComponent.inject(this)
    }
}