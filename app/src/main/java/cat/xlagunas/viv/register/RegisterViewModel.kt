package cat.xlagunas.viv.register

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class RegisterViewModel @Inject constructor(private val userRepository: AuthenticationRepository) : DisposableViewModel() {

    private val user: MutableLiveData<User> = MutableLiveData()

    fun register(user: User): Completable = userRepository.registerUser(user)

    fun findUser(): LiveData<User> {
        disposable.add(userRepository.findUser()
                .subscribe(user::postValue, Timber::e))
        return user
    }


}
