package cat.xlagunas.data.contact.list

import android.content.Context
import android.provider.ContactsContract
import com.squareup.sqlbrite3.QueryObservable
import com.squareup.sqlbrite3.SqlBrite
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneContactsDataSource @Inject constructor(private val context: Context) {

    data class ContactDetails(val id: Long, val name: String?, val email: String?)

    fun queryUsers(): QueryObservable {

        val sqlBrite = SqlBrite.Builder().build()

        val contentResolver = sqlBrite.wrapContentProvider(context.contentResolver, Schedulers.io())

        return contentResolver
                .createQuery(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Contacts.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'", null, ContactsContract.Data.DISPLAY_NAME + " DESC ", true)
//                .mapToList {
//                    ContactDetails(
//                            it.getLong(it.getColumnIndex(ContactsContract.Data._ID)),
//                            it.getString(it.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)),
//                            it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
//                    )
//                }
    }

}