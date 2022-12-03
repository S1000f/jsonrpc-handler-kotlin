package dispatcher

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import util.Helpers
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JacksonParserTest {

    @Test
    fun readTreeTest() {
        val json0 = """
            {"name":"nick","num":33}
        """.trimIndent()

        val readTree = Helpers.getMapper().readTree(json0)
        assertEquals("""{"name":"nick","num":33}""", readTree.toString())

        val readTree1 = JacksonParser.readTree(json0)
        assertEquals("""{"name":"nick","num":33}""", readTree1.toString())
    }

    @Test
    fun isArrayTest() {
        val json = """
            [1,2,3]
        """.trimIndent()

        val jsonFalse = """
            {"not":"array"}
        """.trimIndent()

        val node = Helpers.getMapper().readTree(json)
        assertTrue(node.isArray)
        val node1 = Helpers.getMapper().readTree(jsonFalse)
        assertFalse(node1.isArray)

        val readTree = JacksonParser.readTree(json)
        assertTrue(readTree.isArray())
        val readTree1 = JacksonParser.readTree(jsonFalse)
        assertFalse(readTree1.isArray())
    }

    @Test
    fun isEmptyTest() {
        val json = """
            []
        """.trimIndent()

        val json1 = """
            {}
        """.trimIndent()

        val mapper = Helpers.getMapper()
        assertTrue(mapper.readTree(json).isEmpty)
        assertTrue(mapper.readTree(json1).isEmpty)

        assertTrue(JacksonParser.readTree(json).isEmpty())
        assertTrue(JacksonParser.readTree(json1).isEmpty())
    }

    @Test
    fun isIntegralNumberTest() {
        val json = """
            {"num":33}
        """.trimIndent()

        val json1 = """
            {"str":"strings"}
        """.trimIndent()

        JacksonParser.readTree(json)
            .findValue("num")
            .let {
                assertNotNull(it)
                assertTrue(it.isIntegralNumber())
            }

        JacksonParser.readTree(json1)
            .findValue("str")
            .let {
                assertNotNull(it)
                assertFalse(it.isIntegralNumber())
            }
    }

    @Test
    fun findValueTest() {
        val json0 = """
            {"foo":"bar","num":42,"obj":{"name":"nick"},"arr":[1,2,3]}
        """.trimIndent()

        val mapper = Helpers.getMapper()
        val readTree = mapper.readTree(json0)
        val foo = readTree.findValue("foo")
        val num = readTree.findValue("num")
        val obj = readTree.findValue("obj")
        val arr = readTree.findValue("arr")

        assertEquals("bar", foo.textValue())
        assertEquals("bar", foo.asText())


        val holder = JacksonParser.readTree(json0)
        val fooHolder = holder.findValue("foo")
        val numHolder = holder.findValue("num")
        val objHolder = holder.findValue("obj")
        val arrHolder = holder.findValue("arr")

        assertEquals("bar", fooHolder?.textValue())
        assertEquals("bar", fooHolder?.asText())

    }

}