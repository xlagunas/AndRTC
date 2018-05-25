package cat.xlagunas.data.contact.list

import cat.xlagunas.data.common.net.FriendDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ContactsApi {

    @GET("/contact/search/{filter}")
    fun searchContact(@Path("filter") query: String): Single<List<FriendDto>>
}
