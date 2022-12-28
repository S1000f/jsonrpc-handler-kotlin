package jsonrpc.util

import com.fasterxml.jackson.core.type.TypeReference
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HelpersTest {

    @Test
    fun test() {
        val typeRef = object : TypeReference<List<Int>>() {}
        val deserialize = Helpers.deserialize("""[1, 2, 3]""", object : TypeReference<List<Int>>() {})
    }
}