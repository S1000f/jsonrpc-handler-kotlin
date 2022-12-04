package dispatcher

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
        val spec = Specification.V2_0

        val build = spec.builder(jsonNode)

        assertNull(build)

        Specification.contextBuilder()(jsonNode).let {
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
        val spec = Specification.V2_0

        val build = spec.builder(jsonNode)

        assertNull(build)

        Specification.contextBuilder()(jsonNode).let {
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
        val spec = Specification.V2_0

        val build = spec.builder(jsonNode)

        assertNull(build)

        Specification.contextBuilder()(jsonNode).let {
            assertAll(
                { assertTrue(it.isDone()) },
                { assertNotNull(it) },
            )

            val responses = it.getResponses()

            assertEquals(1, responses.size)
            assertFalse(responses.first().isSuccess())
        }
    }

}