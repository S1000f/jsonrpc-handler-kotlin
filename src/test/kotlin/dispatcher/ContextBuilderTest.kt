package dispatcher

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContextBuilderTest {

    private val _jsonEmptyArray = "[]"
    private val _jsonInvalidBatch0 = "[1]"
    private val _jsonInvalidBatch1 = "[1,30,2]"

    @Test
    fun batchEmptyArrayTest() {
        val jsonNode = JacksonParser.readTree(_jsonEmptyArray)
        val spec = Specification.V2_0

        val build = spec.build(jsonNode)

        assertTrue { build != null }
        build!!.getRequests().let { assertTrue { it.isEmpty() } }
        assertEquals(1, build.getResponses().size)
        assertTrue { build.isDone() }
    }

    @Test
    fun invalidBatchArrayTest() {
        val jsonNode = JacksonParser.readTree(_jsonInvalidBatch0)
        val spec = Specification.V2_0

        val build = spec.build(jsonNode)

        assertTrue { build != null }
        build!!.getRequests().let { assertTrue { it.isEmpty() } }
        assertEquals(1, build.getResponses().size)
        assertTrue { build.isDone() }
    }

    @Test
    fun invalidBatchArrayTest1() {
        val jsonNode = JacksonParser.readTree(_jsonInvalidBatch1)
        val spec = Specification.V2_0

        val build = spec.build(jsonNode)

        assertTrue { build != null }
        build!!.getRequests().let { assertTrue { it.isEmpty() } }
        assertEquals(3, build.getResponses().size)
        assertTrue { build.isDone() }
    }

}