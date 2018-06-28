package cat.xlagunas.viv

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import cat.xlagunas.viv.contact.Display
import cat.xlagunas.viv.contact.DisplayState
import cat.xlagunas.viv.contact.NotRegistered
import javax.inject.Inject

class ContactViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository) : DisposableViewModel() {

    val getUserInfo: LiveData<DisplayState> by lazy {
        val liveData = MutableLiveData<DisplayState>()
        disposable.add(authenticationRepository.findUser().subscribe({ user -> liveData.postValue(Display(user)) }, { Error(it.localizedMessage) }, { liveData.postValue(NotRegistered) }))
        liveData
    }

}