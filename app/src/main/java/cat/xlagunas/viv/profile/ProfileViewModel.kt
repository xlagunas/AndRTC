package cat.xlagunas.viv.profile

import cat.xlagunas.domain.user.authentication.AuthenticationRepository
import cat.xlagunas.viv.commons.DisposableViewModel
import cat.xlagunas.viv.commons.extension.toLiveData
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : DisposableViewModel() {

    val loggedInStatus = this.authenticationRepository.isUserLoggedIn().toLiveData()
    val user = this.authenticationRepository.getUser().toLiveData()
}