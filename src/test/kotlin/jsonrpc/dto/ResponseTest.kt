package jsonrpc.dto

import com.fasterxml.jackson.core.type.TypeReference
import jsonrpc.dto.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import jsonrpc.util.Helpers
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResponseTest {

    @Test
    fun errorFieldDeserializingTest() {
        val json = """
            {"code": -32600, "message": "Invalid Request"}
        """.trimIndent()

        val error = Helpers.deserialize(json, object : TypeReference<ErrorField<Any>>() {})

        assertNotNull(error)
        assertEquals(-32600, error.code)
        assertEquals("Invalid Request", error.message)
        assertNull(error.data)

        val json1 = """
            {"code": -32600, "message": "Invalid Request", "data": "some"}
        """.trimIndent()

        val error1 = Helpers.deserialize(json1, object : TypeReference<ErrorField<String>>() {})

        assertNotNull(error1)
        assertEquals(-32600, error1.code)
        assertEquals("Invalid Request", error1.message)
        assertEquals("some", error1.data)

        val json2 = """
            {"code": -32600, "message": "Invalid Request", "data": 1}
        """.trimIndent()

        val error2 = Helpers.deserialize(json2, object : TypeReference<ErrorField<Int>>() {})

        assertNotNull(error2)
        assertEquals(-32600, error2.code)
        assertEquals("Invalid Request", error2.message)
        assertEquals(1, error2.data)

        val json3 = """
            {"code": -32600, "message": "Invalid Request", "data": {"a": 1}}
        """.trimIndent()

        val error3 = Helpers.deserialize(json3, object : TypeReference<ErrorField<Map<String, Int>>>() {})

        assertNotNull(error3)
        assertEquals(-32600, error3.code)
        assertEquals("Invalid Request", error3.message)
        assertEquals(1, error3.data?.get("a"))

        val json4 = """
            {"code": -32600, "message": "Invalid Request", "data": [1,2,3]}
        """.trimIndent()

        val error4 = Helpers.deserialize(json4, object : TypeReference<ErrorField<List<Int>>>() {})

        assertNotNull(error4)
        assertEquals(-32600, error4.code)
        assertEquals("Invalid Request", error4.message)
        assertEquals(1, error4.data?.get(0))
        assertEquals(2, error4.data?.get(1))
        assertEquals(3, error4.data?.get(2))
    }

    @Test
    fun errorFieldSerializingTest() {
        val error = ErrorField<Any>(-32600, "Invalid Request", null)
        val json = Helpers.serialize(error)

        assertNotNull(json)
        assertEquals("""{"code":-32600,"message":"Invalid Request","data":null}""", json)

        val error1 = ErrorField(-32600, "Invalid Request", "some")
        val json1 = Helpers.serialize(error1)

        assertNotNull(json1)
        assertEquals("""{"code":-32600,"message":"Invalid Request","data":"some"}""", json1)

        val error2 = ErrorField(-32600, "Invalid Request", 1)
        val json2 = Helpers.serialize(error2)

        assertNotNull(json2)
        assertEquals("""{"code":-32600,"message":"Invalid Request","data":1}""", json2)

        val error3 = ErrorField(-32600, "Invalid Request", mapOf("a" to 1))
        val json3 = Helpers.serialize(error3)

        assertNotNull(json3)
        assertEquals("""{"code":-32600,"message":"Invalid Request","data":{"a":1}}""", json3)

        val error4 = ErrorField(-32600, "Invalid Request", listOf(1, 2, 3))
        val json4 = Helpers.serialize(error4)

        assertNotNull(json4)
        assertEquals("""{"code":-32600,"message":"Invalid Request","data":[1,2,3]}""", json4)
    }

    @Test
    fun responseErrorTest() {
        val error = Response.error(PresetError.INVALID_REQUEST)

        val serialize = Helpers.serialize(error)

        assertNotNull(serialize)
        assertEquals(
            """{"error":{"code":-32600,"message":"Invalid Request","data":null},"jsonrpc":"2.0","id":"0"}""",
            serialize
        )

        val toJson = error.toJson()

        assertNotNull(toJson)
        assertEquals(serialize, toJson)

        val error1 = Response.error(PresetError.INVALID_REQUEST)
        val list = listOf(error, error1)

        val serialize1 = Helpers.serialize(list)

        assertNotNull(serialize1)
        assertEquals(
            """[{"error":{"code":-32600,"message":"Invalid Request","data":null},"jsonrpc":"2.0","id":"0"},{"error":{"code":-32600,"message":"Invalid Request","data":null},"jsonrpc":"2.0","id":"0"}]""",
            serialize1
        )
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun responseErrorDeserializingTest() {
        val json = """
            {"error":{"code":-32600,"message":"Invalid Request","data":null},"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response = Helpers.deserialize(json, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(response)
        assertEquals("2.0", response.jsonrpc)
        assertEquals("0", response.id)
        assertNotNull(response.error)
        assertEquals(-32600, response.error.code)
        assertEquals("Invalid Request", response.error.message)
        assertNull(response.error.data)

        val json1 = """
            {"error":{"code":-32600,"message":"Invalid Request","data":"some"},"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response1 = Helpers.deserialize(json1, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(response1)

        val data = response1.error.data

        assertNotNull(data)
        assertDoesNotThrow { data as String }

        val dataString = data as String

        assertEquals("some", dataString)

        val json2 = """
            {"error":{"code":-32600,"message":"Invalid Request","data":{"a":1}},"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response2 = Helpers.deserialize(json2, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(response2)

        val data2 = response2.error.data

        assertNotNull(data2)
        assertDoesNotThrow { data2 as Map<String, Int> }

        val dataMap = data2 as Map<String, Int>

        assertEquals(1, dataMap["a"])

        val json3 = """
            {"error":{"code":-32600,"message":"Invalid Request","data":[1,2,3]},"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response3 = Helpers.deserialize(json3, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(response3)

        val data3 = response3.error.data

        assertNotNull(data3)
        assertDoesNotThrow { data3 as List<Int> }

        val dataList = data3 as List<Int>

        assertEquals(1, dataList[0])
        assertEquals(2, dataList[1])
        assertEquals(3, dataList[2])
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun responseSuccessDeserializingTest() {
        val json1 = """
            {"result":"some","jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response1 = Helpers.deserialize(json1, object : TypeReference<ResponseSuccess<Any>>() {})

        assertNotNull(response1)
        assertEquals("some", response1.result as String)

        val json2 = """
            {"result":{"a":1},"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response2 = Helpers.deserialize(json2, object : TypeReference<ResponseSuccess<Any>>() {})

        assertNotNull(response2)
        assertEquals(1, (response2.result as Map<String, Int>)["a"])

        val json3 = """
            {"result":[1,2,3],"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response3 = Helpers.deserialize(json3, object : TypeReference<ResponseSuccess<Any>>() {})

        assertNotNull(response3)
        assertEquals(1, (response3.result as List<Int>)[0])
        assertEquals(2, (response3.result as List<Int>)[1])
        assertEquals(3, (response3.result as List<Int>)[2])
    }

    @Test
    fun responseSuccessSerializingTest() {
        val response1 = Response.success("some")
        val json1 = Helpers.serialize(response1)

        assertNotNull(json1)
        assertEquals("""{"result":"some","jsonrpc":"2.0","id":"0"}""", json1)

        val response2 = Response.success(mapOf("a" to 1))
        val json2 = Helpers.serialize(response2)

        assertNotNull(json2)
        assertEquals("""{"result":{"a":1},"jsonrpc":"2.0","id":"0"}""", json2)

        val response3 = Response.success(listOf(1, 2, 3))
        val json3 = Helpers.serialize(response3)

        assertNotNull(json3)
        assertEquals("""{"result":[1,2,3],"jsonrpc":"2.0","id":"0"}""", json3)
    }

    @Test
    fun responseJsonHolderTest() {
        val json = """
            {"result":"some","jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response = Response.fromJson(json)

        assertNotNull(response)
        assertTrue(response.isSuccess())
        assertEquals("some", response.getSuccessInfo() as String)
        assertEquals("2.0", response.getVersion())
        assertEquals("0", response.getResponseId())
        assertEquals(json, response.toJson())

        val json1 = """
            {"error":{"code":-32600,"message":"Invalid Request","data":null},"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response1 = Response.fromJson(json1)

        assertNotNull(response1)
        assertFalse(response1.isSuccess())
        assertEquals(-32600, response1.getErrorInfo()!!.code)
        assertEquals("Invalid Request", response1.getErrorInfo()!!.message)
        assertNull(response1.getErrorInfo()!!.data)
        assertEquals("2.0", response1.getVersion())
        assertEquals("0", response1.getResponseId())
        assertEquals(json1, response1.toJson())

        val json3 = """
            {"error":{"code":-32600,"message":"Invalid Request","data":"some"},"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response3 = Response.fromJson(json3)

        assertNotNull(response3)
        assertFalse(response3.isSuccess())
        assertEquals(-32600, response3.getErrorInfo()!!.code)
        assertEquals("Invalid Request", response3.getErrorInfo()!!.message)
        assertEquals("some", response3.getErrorInfo()!!.data as String)
        assertEquals("2.0", response3.getVersion())
        assertEquals("0", response3.getResponseId())
        assertEquals(json3, response3.toJson())

        val json2 = """
            {"result":[1,2,3],"jsonrpc":"2.0","id":"0"}
        """.trimIndent()

        val response2 = Response.fromJson(json2)

        assertNotNull(response2)
        assertTrue(response2.isSuccess())
        assertEquals(listOf(1, 2, 3), response2.getSuccessInfo() as List<Int>)
        assertEquals("2.0", response2.getVersion())
        assertEquals("0", response2.getResponseId())
        assertEquals(json2, response2.toJson())
    }

}