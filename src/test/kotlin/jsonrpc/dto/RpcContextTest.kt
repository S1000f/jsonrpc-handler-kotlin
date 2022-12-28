package jsonrpc.dto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RpcContextTest {

    private val reqAdder = Request.of("adder", listOf(1, 2))!!
    private val reqEcho = Request.of("echo", "hello")!!
    private val resAddr = Response.success(3, reqAdder)
    private val resEcho = Response.success("hello", reqEcho)

    @Test
    fun getTest() {
        val contextHolder = RpcContext.of(false, listOf(reqAdder, reqEcho), listOf(resAddr, resEcho))
        val requests = contextHolder.getRequests()

        assertNotNull(requests)
        assertEquals(2, requests.size)
        assertEquals(reqAdder, requests[0])
        assertEquals(reqEcho, requests[1])

        val responses = contextHolder.getResponses()

        assertNotNull(responses)
        assertEquals(2, responses.size)
        assertEquals(resAddr, responses[0])
        assertEquals(resEcho, responses[1])
    }

    @Test
    fun doneTest() {
        val contextHolder = RpcContext.of(false, resAddr)
        assertFalse(contextHolder.isDone())
        assertEquals(1, contextHolder.getResponses().size)

        val done = contextHolder.done()

        assertTrue(done.isDone())
        assertNotEquals(contextHolder, done)

        val done1 = contextHolder.done(listOf(resAddr, resEcho))

        assertTrue(done1.isDone())
        assertNotEquals(contextHolder, done1)
        assertEquals(2, done1.getResponses().size)
    }
}