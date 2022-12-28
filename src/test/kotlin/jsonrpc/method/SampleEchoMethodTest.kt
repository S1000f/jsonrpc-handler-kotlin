package jsonrpc.method

import jsonrpc.dto.RequestImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SampleEchoMethodTest {

    @Test
    fun instanceTest() {
        val method = SampleEchoMethod()

        assertNotNull(method)
        assertNotNull(method as? RpcMethod)
        assertEquals("echo", method.getName())
        assertNull(method.getParamsType())
    }

    @Test
    fun handleMethodTest() {
        val method = SampleEchoMethod()
        val request = RequestImpl.of("echo", null)

        assertNotNull(request)

        val now = Instant.now().epochSecond
        val params = "params"
        val response = method.handle(request, params)

        assertNotNull(response)
        assertTrue(response.isSuccess())
        assertNotNull(response.getSuccessInfo())
        assertNotNull(response.getSuccessInfo() as? Map<*, *>)

        val map = response.getSuccessInfo() as Map<*, *>

        assertEquals("echo", map["method"])
        assertTrue(map["timestamp"] as Long >= now)
    }
}