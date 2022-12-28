package jsonrpc.dispatcher

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContextBuilderTest {

    private val _jsonEmptyArray = "[]"
    private val _jsonInvalidBatch0 = "[1]"
    private val _jsonInvalidBatch1 = "[1,30,2]"

    @Test
    fun batchEmptyArrayTest() {
        val jsonNode = JacksonParser.readTree(_jsonEmptyArray)

        Specification.contextBuilder().builder(jsonNode)?.let {
            assertAll(
                { assertTrue(it.isDone()) },
                { assertNotNull(it) },
            )

            val responses = it.getResponses()

            assertEquals(1, responses.size)
            assertFalse(responses.first().isSuccess())
        }
    }

    @Test
    fun invalidBatchArrayTest() {
        val jsonNode = JacksonParser.readTree(_jsonInvalidBatch0)

        Specification.contextBuilder().builder(jsonNode)?.let {
            assertAll(
                { assertTrue(it.isDone()) },
                { assertNotNull(it) },
            )

            val responses = it.getResponses()

            assertEquals(1, responses.size)
            assertFalse(responses.first().isSuccess())
        }
    }

    @Test
    fun invalidBatchArrayTest1() {
        val jsonNode = JacksonParser.readTree(_jsonInvalidBatch1)

        Specification.contextBuilder().builder(jsonNode)?.let {
            assertAll(
                { assertTrue(it.isDone()) },
                { assertNotNull(it) },
            )

            val responses = it.getResponses()

            assertEquals(3, responses.size)
            assertFalse(responses.first().isSuccess())
            assertFalse(responses[1].isSuccess())
            assertFalse(responses.last().isSuccess())
        }
    }

}