package cat.xlagunas.viv.contact

import cat.xlagunas.domain.commons.Friend

interface ContactListener {

    fun onContactRequested(friend: Friend)
    fun onContactAccepted(friend: Friend)
    fun onContactRejected(friend: Friend)
    fun onContactCalled(friend: Friend)
}