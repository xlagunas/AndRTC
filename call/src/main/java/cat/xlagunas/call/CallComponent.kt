package cat.xlagunas.call

import dagger.Subcomponent

@Subcomponent(modules = [CallModule::class])
interface CallComponent {

    fun inject(fragment: CallConfirmationDialog)
}
