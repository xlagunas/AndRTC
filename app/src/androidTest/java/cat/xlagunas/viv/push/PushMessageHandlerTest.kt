package cat.xlagunas.viv.push

import org.junit.Before
import org.junit.Test

class PushMessageHandlerTest {

    lateinit var pushMessageHandler: PushMessageHandler

    @Before
    fun setUp() {
        pushMessageHandler = PushMessageHandler()
    }

    @Test
    fun whenContactsMessage_thenContactsMessageProcessor(){

    }

    @Test
    fun whenNullMessage_thenNothingProcessed(){

    }

    @Test
    fun whenCallMessage_thenCallProcessor(){

    }
}