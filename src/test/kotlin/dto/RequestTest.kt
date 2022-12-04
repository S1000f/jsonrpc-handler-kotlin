package dto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import util.Helpers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestTest {

    private val _jsonWithParamsByPosition = """
        {"jsonrpc":"2.0","method":"subtract","params":[42,23],"id":1}
    """.trimIndent()

    private val _jsonWithNoParams = """
        {"jsonrpc":"2.0","method":"no-params","id":1}
    """.trimIndent()

    private val _jsonNotification = """
        {"jsonrpc":"2.0","method":"notification","params":"hi"}
    """.trimIndent()

    private val _jsonBatch = """
        [{"jsonrpc":"2.0","method":"subtract","params":[42,23],"id":1},{"jsonrpc":"2.0","method":"subtract","params":[12,10],"id":2}]
    """.trimIndent()

    @Test
    fun scaffoldClassTest() {
        val scaffold = Scaffold("noParams", null)
        val json = scaffold.toJson()

        assertNotNull(json)

        val node = Helpers.getMapper().readTree(json)

        assertNotNull(node)

        node.findValue("method")?.let {
            assertEquals("noParams", it.asText())
        }

        node.findValue("jsonrpc")?.let {
            assertEquals("2.0", it.asText())
        }

        node.findValue("id")?.let {
            assertEquals(0, it.asInt())
        }
    }

    @Test
    fun scaffoldAndRequestClassTest() {
        val paramsInput = mapOf("currency" to "eth")

        val scaffold = Scaffold("name", paramsInput)
        val toJson = scaffold.toJson()

        assertNotNull(toJson)

        val parameters = scaffold.getParameters()

        assertNotNull(parameters)
        assertEquals("""{"currency":"eth"}""", parameters)

        Helpers.getMapper().readTree(parameters)?.let { it ->
            it.findValue("currency")?.let {
                assertEquals("eth", it.asText())
            }
        }
    }

    @Test
    fun requestImplTest() {
        val request = RequestImpl.of("subtract", listOf(42, 23))

        assertNotNull(request)
        assertEquals("subtract", request.getMethodName())
        assertEquals("2.0", request.getVersion())
        assertEquals("0", request.getRequestId())
        request.getParameters()?.let {
            assertEquals("[42,23]", it)
        }
    }

    @Test
    fun requestImplToJsonTest() {
        val request = RequestImpl.of("subtract", listOf(42, 23))

        assertNotNull(request)

        val json = request.toJson()

        assertNotNull(json)

        val node = Helpers.getMapper().readTree(json)

        assertNotNull(node)

        node.findValue("method")?.let {
            assertEquals("subtract", it.asText())
        }

        node.findValue("jsonrpc")?.let {
            assertEquals("2.0", it.asText())
        }

        node.findValue("id")?.let {
            assertEquals("0", it.asText())
        }

        node.findValue("params")?.let {
            assertEquals("""[42,23]""", it.toString())
        }
    }

}