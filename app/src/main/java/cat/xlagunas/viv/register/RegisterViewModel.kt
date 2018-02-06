package cat.xlagunas.viv.register

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.ViewModel
import cat.xlagunas.domain.commons.User
import cat.xlagunas.domain.user.register.RegisterRepository
import io.reactivex.Completable
import javax.inject.Inject

class RegisterViewModel @Inject constructor(private val userRepository: RegisterRepository) : ViewModel() {

    fun register(user: User): Completable = userRepository.registerUser(user)

    fun findUser(): LiveData<User> = LiveDataReactiveStreams.fromPublisher(userRepository.findUser())

}
