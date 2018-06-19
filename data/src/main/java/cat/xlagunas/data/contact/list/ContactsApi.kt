package cat.xlagunas.data.contact.list

import cat.xlagunas.data.common.net.FriendDto
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ContactsApi {

    @GET("/contact/search/{filter}")
    fun searchContact(@Path("filter") query: String): Single<List<FriendDto>>

    @PUT("/contact/{contactId}/")
    fun addContact(@Path("contactId") contactId: Long): Completable
}
