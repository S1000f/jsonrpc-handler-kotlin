package method

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpRouteMethodTest {

    @Test
    fun instanceTest() {
        val method = HttpRouteMethod("test", "http://api.open-notify.org/astros.json")

        assertNotNull(method)
        assertNotNull(method as? RpcMethod)
        assertEquals("test", method.getName())
        assertEquals("http://api.open-notify.org/astros.json", method.endpoint)
        assertNull(method.getParamsType())
    }

}