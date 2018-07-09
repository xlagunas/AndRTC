package cat.xlagunas.data.contact

import android.content.SharedPreferences
import androidx.core.content.edit
import cat.xlagunas.domain.common.time.TimeProvider
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ContactCacheImpl @Inject constructor(
        private val sharedPreferences: SharedPreferences,
        private val timeProvider: TimeProvider) : ContactCache {

    override fun invalidateCache(): Completable {
        return Completable.fromAction {
            sharedPreferences.edit { remove(LAST_MODIFIED_DATE) }
        }
    }

    override fun updateCache(): Completable {
        return Completable.fromAction {
            sharedPreferences.edit { putLong(LAST_MODIFIED_DATE, timeProvider.getTimeMillis()) }
        }
    }

    override fun isCacheValid(): Single<Boolean> {
        return Single.fromCallable { sharedPreferences.getLong(LAST_MODIFIED_DATE, 0) }
                .map { timeProvider.getTimeMillis() - it <= MAX_VALID_CACHE_MILLIS }
    }

    companion object {
        const val LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE"
        const val MAX_VALID_CACHE_MILLIS = 60 * 60 * 1000L
    }
}