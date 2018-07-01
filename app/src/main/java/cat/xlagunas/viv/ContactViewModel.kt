package cat.xlagunas.viv

import android.arch.lifecycle.MutableLiveData
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import cat.xlagunas.viv.contact.Display
import cat.xlagunas.viv.contact.DisplayState
import cat.xlagunas.viv.contact.NotRegistered
import javax.inject.Inject

class ContactViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository) : DisposableViewModel() {

    private val userLiveData = MutableLiveData<DisplayState>()

    val getUserInfo: MutableLiveData<DisplayState> by lazy {
        disposable.add(authenticationRepository.findUser()
                .subscribe({ userLiveData.postValue(Display(it)) },
                        { Error(it.localizedMessage) },
                        { userLiveData.postValue(NotRegistered) }))
        userLiveData
    }

    init {
        authenticationRepository.getUser().subscribe { getUserInfo.value = Display(it) }
    }

}