package jsonrpc.dispatcher

import com.fasterxml.jackson.core.type.TypeReference
import jsonrpc.dto.*
import jsonrpc.mapper.MethodMapper
import jsonrpc.method.RpcMethod
import jsonrpc.method.SampleEchoMethod
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import jsonrpc.util.Helpers
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("UNCHECKED_CAST")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonrpcHandlerTest {

    class AdderMethod : RpcMethod {
        override fun getName() = "adder"

        override fun getParamsType() = object : TypeReference<List<Int>>() {}

        @Suppress("UNCHECKED_CAST")
        override fun handle(request: Request, params: Any?): Response {
            val list = params as? List<Int> ?: return Response.error(PresetError.INVALID_PARAMS)
            val sum = list.sum()
            return Response.success(sum, request)
        }
    }

    @Test
    fun singleNotificationTest() {
        val json = """{"jsonrpc": "2.0", "method": "echo", "params": "hello"}"""

        val dispatch = JsonrpcHandler(MethodMapper.from(SampleEchoMethod())).dispatch(json)

        assertNull(dispatch)
    }

    @Test
    fun singleRequestMethodNotFoundTest() {
        val json = """{"jsonrpc": "2.0", "method": "abcd", "params": [1,2], "id": 1}"""

        val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(AdderMethod()))
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(deserialize)
        assertEquals(PresetError.METHOD_NOT_FOUND.code, deserialize.getErrorInfo().code)
    }

    @Test
    fun singleRequestInvalidParamsTest() {
        val json = """{"jsonrpc": "2.0", "method": "adder", "params": "adder", "id": 1}"""

        val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(AdderMethod()))
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(deserialize)
        assertEquals(PresetError.INVALID_PARAMS.code, deserialize.getErrorInfo().code)
    }

    @Test
    fun singleRequestParsingInvalidTest() {
        val json = """{"jsonrpc": "2.0","id":42, "method": "adder", "params"}"""

        val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(AdderMethod()))
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(deserialize)
        assertEquals(PresetError.PARSE_ERROR.code, deserialize.getErrorInfo().code)
    }

    @Test
    fun batchSomeSuccessAndSomeFailedAndSomeNotificationTest() {
        val json = """[{"jsonrpc": "2.0", "method": "echo", "params": "hello, world!"},
            {"jsonrpc": "2.0", "method": "adder", "params": [1,2], "id": 2},
            {"jsonrpc":"2.0","method":"adder","params":"fail","id":"12"}]""".trimIndent()

        val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(AdderMethod(), SampleEchoMethod()))
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<List<Response>>() {})

        assertNotNull(deserialize)
        assertEquals(2, deserialize.size)

        val success = deserialize.find { it.isSuccess() }
        val failed = deserialize.find { !it.isSuccess() }

        assertNotNull(success)
        assertNotNull(failed)

        assertEquals(3, success.getSuccessInfo().toString().toInt())
        assertEquals(PresetError.INVALID_PARAMS.code, failed.getErrorInfo()?.code)
        assertEquals("12", failed.getResponseId())
    }

    @Test
    fun batchSomeNotificationAndSomeSuccessTest() {
        val json = """[{"jsonrpc": "2.0", "method": "echo", "params": "hello, world!"},
            {"jsonrpc": "2.0", "method": "adder", "params": [1,2], "id": 2}]""".trimIndent()

        val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(SampleEchoMethod(), AdderMethod()))
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<List<Response>>() {})

        assertNotNull(deserialize)
        assertEquals(1, deserialize.size)

        val response = deserialize.first()

        assertDoesNotThrow { response as ResponseSuccess<Int> }

        val responseSuccess = response as ResponseSuccess<Int>

        assertEquals(3, responseSuccess.result)
    }

    @Test
    fun batchAllNotificationTest() {
        val json = """
            [
                {"jsonrpc": "2.0", "method": "echo", "params": "hello, world!"},
                {"jsonrpc": "2.0", "method": "echo", "params": "hello, world!"}
            ]
        """.trimIndent()

        val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(SampleEchoMethod()))
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNull(dispatch)
    }

    @Test
    fun batchSomeSuccessAndSomeInvalidTest() {
        val json = """[
            {"jsonrpc": "2.0", "method": "echo", "params": 42, "id": 1},
            {"jsonrpc": "2.0", "method": "adder", "params": 3, "id": 2}]""".trimIndent()

        val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(AdderMethod(), SampleEchoMethod()))
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<List<Response>>() {})

        assertNotNull(deserialize)
        assertEquals(2, deserialize.size)

        val success = deserialize.find { it.isSuccess() }
        val failed = deserialize.find { !it.isSuccess() }

        assertNotNull(success)
        assertNotNull(failed)

        assertDoesNotThrow { success as ResponseSuccess<Map<String, Any>> }
        assertDoesNotThrow { failed as ResponseError<Any> }

        val resFailed = failed as ResponseError<Any>

        assertEquals(PresetError.INVALID_PARAMS.code, resFailed.error.code)
    }

    @Test
    fun batchAllSuccessTest() {
        val json = """[
            {"jsonrpc": "2.0", "method": "echo", "params": 42, "id": 1},
            {"jsonrpc": "2.0", "method": "adder", "params": [1, 2], "id": 2}
        ]""".trimIndent()

        val mapper = MethodMapper.from(AdderMethod(), SampleEchoMethod())
        val jsonrpcHandler = JsonrpcHandler(mapper)

        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val response = Helpers.deserialize(dispatch, object : TypeReference<List<ResponseSuccess<Any>>>() {})

        assertNotNull(response)
        assertEquals(2, response.size)

        val res1 = response.find { it.id == "1" }
        val res2 = response.find { it.id == "2" }

        assertNotNull(res1)
        assertNotNull(res2)

        val map = res1.getSuccessInfo() as Map<String, Any>

        println(map)

        assertEquals("echo", map["method"])
        assertEquals(3, res2.result)
    }

    @Test
    fun batchMalformedJsonInvalidTest() {
        val json = """[{"jsonrpc":"2.0","method":"subtract","params":[42,23],"id":1},{"jsonrpc":"2.0","method"}]"""
        val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(SampleEchoMethod()))
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(deserialize)
        assertEquals(-32700, deserialize.getErrorInfo().code)
    }

    @Test
    fun batchEmptyInvalidTest() {
        val json = "[]"
        val handler = JsonrpcHandler(MethodMapper.empty())
        val response = handler.dispatch(json)

        assertNotNull(response)

        val deserialize = Helpers.deserialize(response, object : TypeReference<ResponseError<Any>>() {})

        assertNotNull(deserialize)
        assertEquals(-32600, deserialize.error.code)
        assertEquals("Invalid Request", deserialize.error.message)
        assertEquals(null, deserialize.error.data)
    }

    @Test
    fun batchSingleInvalidTest() {
        val json = """[1]"""
        val handler = JsonrpcHandler(MethodMapper.empty())
        val dispatch = handler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<List<ResponseError<Any>>>() {})

        assertNotNull(deserialize)
        assertEquals(1, deserialize.size)
        assertEquals(-32600, deserialize[0].error.code)
    }

    @Test
    fun batchAllInvalidTest() {
        val json = "[1,2,3]"
        val jsonrpcHandler = JsonrpcHandler(MethodMapper.empty())
        val dispatch = jsonrpcHandler.dispatch(json)

        assertNotNull(dispatch)

        val deserialize = Helpers.deserialize(dispatch, object : TypeReference<List<ResponseError<Any>>>() {})

        assertNotNull(deserialize)
        assertEquals(3, deserialize.size)
        assertEquals(-32600, deserialize[0].error.code)
        assertEquals(-32600, deserialize[1].error.code)
        assertEquals(-32600, deserialize[2].error.code)
    }
}