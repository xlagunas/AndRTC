package cat.xlagunas.data.contact

import io.reactivex.Completable
import io.reactivex.Single

interface ContactCache {

    fun isCacheValid(): Single<Boolean>
    fun invalidateCache(): Completable
    fun updateCache(): Completable

}