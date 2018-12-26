package cat.xlagunas.conference.data

import kotlin.test.Test
import kotlin.test.assertEquals


class UUIDUserSessionTest{

    private val userSession = UUIDUserSession()

    @Test
    fun givenMultipleCalls_thenSameString(){
        val firstCall = userSession.getUserId()
        val secondCall = userSession.getUserId()

        assertEquals(firstCall, secondCall)
    }
}