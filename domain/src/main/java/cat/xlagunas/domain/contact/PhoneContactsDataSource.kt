package cat.xlagunas.domain.contact

import io.reactivex.Flowable
import io.reactivex.Observable

interface PhoneContactsDataSource {
    fun getUserPhoneContacts(): Flowable<List<ContactDetails>>
}