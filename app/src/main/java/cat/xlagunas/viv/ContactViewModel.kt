package cat.xlagunas.viv

import android.arch.lifecycle.MutableLiveData
import cat.xlagunas.data.contact.list.PhoneContactsDataSource
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import cat.xlagunas.viv.contact.Display
import cat.xlagunas.viv.contact.DisplayState
import cat.xlagunas.viv.contact.Error
import cat.xlagunas.viv.contact.NotRegistered
import timber.log.Timber
import javax.inject.Inject

class ContactViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository,
                                           private val phoneContactsDataSource: PhoneContactsDataSource) : DisposableViewModel() {

    val displayState by lazy {
        MutableLiveData<DisplayState>()
    }

    fun getUserInfo() {
        authenticationRepository.isAuthTokenAvailable()
        disposable.addAll(authenticationRepository.findUser()
                .subscribe(
                        { displayState.postValue(Display(it)) },
                        { displayState.postValue(Error(it.localizedMessage)) },
                        { displayState.postValue(NotRegistered) }
                ))
    }

    fun phoneContactDataSource() {
        disposable.addAll(phoneContactsDataSource
                .queryUsers()
                .subscribe({ Timber.d("Total size: %s", it.size) }, Timber::e))
    }


}