package cat.xlagunas.contact.ui

import cat.xlagunas.contact.domain.Friend

interface ContactListener {

    fun onContactRequested(friend: Friend)
    fun onContactAccepted(friend: Friend)
    fun onContactRejected(friend: Friend)
    fun onContactCalled(friend: Friend)
}