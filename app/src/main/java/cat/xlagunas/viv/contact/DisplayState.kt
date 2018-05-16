package cat.xlagunas.viv.contact

import cat.xlagunas.domain.commons.User

sealed class DisplayState

object Loading : DisplayState()
data class Error(val message: String) : DisplayState()
data class Display(val user: User) : DisplayState()
object NotRegistered : DisplayState()
