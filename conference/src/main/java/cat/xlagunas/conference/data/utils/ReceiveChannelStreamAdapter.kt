/*
 * © 2013 - 2018 Tinder, Inc., ALL RIGHTS RESERVED
 */

package cat.xlagunas.conference.data.utils

import com.tinder.scarlet.Stream
import com.tinder.scarlet.StreamAdapter
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.reactive.openSubscription

@Deprecated("This is to be deprecated once the API from the library gets updated")
class ReceiveChannelStreamAdapter<T> : StreamAdapter<T, ReceiveChannel<T>> {

    override fun adapt(stream: Stream<T>) = stream.openSubscription()
}