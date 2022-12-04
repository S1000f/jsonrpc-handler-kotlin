package dispatcher

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import util.Helpers
import kotlin.test.*

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

        val readTree = JacksonParser.readTree(json)
        assertNotNull(readTree)
        assertTrue(readTree.isArray())

        val readTree1 = JacksonParser.readTree(jsonFalse)
        assertNotNull(readTree1)
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

        assertTrue(JacksonParser.readTree(json)?.isEmpty() ?: false)
        assertTrue(JacksonParser.readTree(json1)?.isEmpty() ?: false)
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
            ?.findValue("num")
            .let {
                assertNotNull(it)
                assertTrue(it.isIntegralNumber())
            }

        JacksonParser.readTree(json1)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertFalse(it.isIntegralNumber())
            }
    }

    @Test
    fun isTextualTest() {
        val json = """
            {"str":"strings"}
        """.trimIndent()

        val json1 = """
            {"num":33}
        """.trimIndent()

        JacksonParser.readTree(json)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertTrue(it.isTextual())
            }

        JacksonParser.readTree(json1)
            ?.findValue("num")
            .let {
                assertNotNull(it)
                assertFalse(it.isTextual())
            }
    }

    @Test
    fun isNullTest() {
        val json = """
            {"str":null}
        """.trimIndent()

        val json1 = """
            {"num":33}
        """.trimIndent()

        JacksonParser.readTree(json)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertTrue(it.isNull())
            }

        JacksonParser.readTree(json1)
            ?.findValue("num")
            .let {
                assertNotNull(it)
                assertFalse(it.isNull())
            }
    }

    @Test
    fun asTextTest() {
        val json = """
            {"str":"strings"}
        """.trimIndent()

        val json1 = """
            {"num":33}
        """.trimIndent()

        val json2 = """
            {"str":null}
        """.trimIndent()

        val json3 = """
            {"str":[1,2,3]}
        """.trimIndent()

        JacksonParser.readTree(json)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertEquals("strings", it.asText())
            }

        JacksonParser.readTree(json1)
            ?.findValue("num")
            .let {
                assertNotNull(it)
                assertEquals("33", it.asText())
            }

        JacksonParser.readTree(json2)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertEquals("null", it.asText())
            }

        JacksonParser.readTree(json3)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertEquals("", it.asText())
            }
    }

    @Test
    fun textValueTest() {
        val json = """
            {"str":"strings"}
        """.trimIndent()

        val json1 = """
            {"num":33}
        """.trimIndent()

        val json2 = """
            {"str":null}
        """.trimIndent()

        val json3 = """
            {"str":[1,2,3]}
        """.trimIndent()

        JacksonParser.readTree(json)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertEquals("strings", it.textValue())
            }

        JacksonParser.readTree(json1)
            ?.findValue("num")
            .let {
                assertNotNull(it)
                assertNull(it.textValue())
            }

        JacksonParser.readTree(json2)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertNull(it.textValue())
            }

        JacksonParser.readTree(json3)
            ?.findValue("str")
            .let {
                assertNotNull(it)
                assertNull(it.textValue())
            }
    }

    @Test
    fun findValueTest() {
        val json0 = """
            {"foo":"bar","num":42,"obj":{"name":"nick"},"arr":[1,2,3]}
        """.trimIndent()

        val holder = JacksonParser.readTree(json0)
        assertNotNull(holder)

        val fooHolder = holder.findValue("foo")
        val numHolder = holder.findValue("num")
        val objHolder = holder.findValue("obj")
        val arrHolder = holder.findValue("arr")

        assertEquals("bar", fooHolder?.textValue())
        assertEquals("bar", fooHolder?.asText())
        assertTrue(numHolder?.isIntegralNumber() ?: false)
        assertEquals("""{"name":"nick"}""", objHolder?.toString())
        assertEquals("nick", objHolder?.findValue("name")?.textValue())
        assertEquals("[1,2,3]", arrHolder?.toString())
        assertTrue { arrHolder?.isArray() ?: false }
    }

    @Test
    fun iteratorTest() {
        val json0 = """
            {"foo":"bar","num":42,"obj":{"name":"nick"},"arr":[1,2,3]}
        """.trimIndent()

        val holder = JacksonParser.readTree(json0)
        assertNotNull(holder)

        val iterator = holder.iterator()
        assertNotNull(iterator)
        assertTrue(iterator.hasNext())
        assertEquals("bar", iterator.next().asText())
        assertEquals("42", iterator.next().asText())
        assertEquals("nick", iterator.next().findValue("name")?.textValue())

        val arr = iterator.next()
        assertEquals("[1,2,3]", arr.toString())
        assertFalse(iterator.hasNext())

        val arrIterator = arr.iterator()
        assertNotNull(arrIterator)
        assertTrue(arrIterator.hasNext())
        assertEquals("1", arrIterator.next().asText())
        assertEquals("2", arrIterator.next().asText())
        assertEquals("3", arrIterator.next().asText())
        assertFalse(arrIterator.hasNext())
    }

}