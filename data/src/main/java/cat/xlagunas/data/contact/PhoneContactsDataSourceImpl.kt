package cat.xlagunas.data.contact

import android.content.Context
import android.provider.ContactsContract
import cat.xlagunas.domain.contact.ContactDetails
import cat.xlagunas.domain.contact.PhoneContactsDataSource
import com.squareup.sqlbrite3.SqlBrite
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PhoneContactsDataSourceImpl @Inject constructor(private val context: Context) : PhoneContactsDataSource {

    override fun getUserPhoneContacts(): Flowable<List<ContactDetails>> {

        val sqlBrite = SqlBrite.Builder().build()

        val contentResolver = sqlBrite.wrapContentProvider(context.contentResolver, Schedulers.io())

        val selection =
            ContactsContract.Contacts.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'"

        return contentResolver
            .createQuery(
                ContactsContract.Data.CONTENT_URI,
                null,
                selection,
                null,
                ContactsContract.Data.DISPLAY_NAME + " DESC ",
                true
            )
            .mapToList {
                ContactDetails(
                    it.getLong(it.getColumnIndex(ContactsContract.Data._ID)),
                    it.getString(it.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)),
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                )
            }.toFlowable(BackpressureStrategy.BUFFER)
    }
}