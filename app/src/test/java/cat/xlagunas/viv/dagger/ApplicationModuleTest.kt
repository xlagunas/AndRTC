package cat.xlagunas.viv.dagger

import kotlinx.coroutines.Dispatchers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ApplicationModuleTest {
    @Test
    fun givenApplicationModule_whenProvideCoroutineDispatchers_thenUsesStandardDispatchers() {
        val dispatchers = ApplicationModule().provideCoroutineDispatchers()

        assertThat(dispatchers.io).isSameAs(Dispatchers.IO)
        assertThat(dispatchers.main).isSameAs(Dispatchers.Main)
        assertThat(dispatchers.default).isSameAs(Dispatchers.Default)
    }
}
