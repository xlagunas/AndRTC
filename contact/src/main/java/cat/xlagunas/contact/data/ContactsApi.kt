package cat.xlagunas.contact.data

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ContactsApi {

    @GET("/contact/search/{filter}")
    fun searchContact(@Path("filter") query: String): Single<List<FriendDto>>

    @PUT("/contact/{contactId}")
    fun addContact(@Path("contactId") contactId: Long): Completable

    @POST("/contact/{contactId}/accept")
    fun acceptContact(@Path("contactId") contactId: Long): Completable

    @POST("/contact/{contactId}/reject")
    fun rejectContact(@Path("contactId") contactId: Long): Completable

    @GET("/contact/list")
    fun listContacts(): Single<List<FriendDto>>
}
