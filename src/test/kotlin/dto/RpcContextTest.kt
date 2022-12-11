package dto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RpcContextTest {

    private val reqAdder = Request.of("adder", listOf(1, 2))!!
    private val reqEcho = Request.of("echo", "hello")!!
    private val resAddr = Response.success(3, reqAdder)
    private val resEcho = Response.success("hello", reqEcho)

    @Test
    fun getRequestsTest() {
        val contextHolder = RpcContext.of(false, listOf(reqAdder), listOf(resAddr))
        val requests = contextHolder.getRequests()

        assertNotNull(requests)
        assertEquals(1, requests.size)

        val first = requests.first()
        assertEquals(reqAdder, first)
    }
}