package cat.xlagunas.data.contact.list

import android.content.Context
import android.provider.ContactsContract
import com.squareup.sqlbrite3.SqlBrite
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneContactsDataSource @Inject constructor(private val context: Context) {

    data class ContactDetails(val id: Long, val name: String?, val email: String?)

    fun queryUsers(): Observable<MutableList<ContactDetails>> {

        val sqlBrite = SqlBrite.Builder().build()

        val contentResolver = sqlBrite.wrapContentProvider(context.contentResolver, Schedulers.io())

        val selection = ContactsContract.Contacts.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'"
//        val selection = ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'"


        return contentResolver
                .createQuery(ContactsContract.Data.CONTENT_URI, null, selection, null, ContactsContract.Data.DISPLAY_NAME + " DESC ", true)
                .mapToList {
                    PhoneContactsDataSource.ContactDetails(
                            it.getLong(it.getColumnIndex(ContactsContract.Data._ID)),
                            it.getString(it.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)),
                            it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                    )
                }
    }

}