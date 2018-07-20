package cat.xlagunas.domain.contact

import io.reactivex.Flowable

interface PhoneContactsDataSource {
    fun getUserPhoneContacts(): Flowable<List<ContactDetails>>
}