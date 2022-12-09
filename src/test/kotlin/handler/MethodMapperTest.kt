package handler

import com.fasterxml.jackson.core.type.TypeReference
import dto.PresetError
import dto.Request
import dto.RequestImpl
import dto.Response
import method.RpcMethod
import method.SampleEchoMethod
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Suppress("UNCHECKED_CAST")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MethodMapperTest {

    class AdderMethod : RpcMethod {
        override fun getName() = "adder"

        override fun getParamsType() = object : TypeReference<List<Int>>() {}

        override fun handle(request: Request, params: Any?): Response {
            val list = params as? List<Int> ?: return Response.error(PresetError.INVALID_PARAMS)
            val sum = list.sum()
            return Response.success(sum, request)
        }
    }

    @Test
    fun addMethodsTest() {
        val mapper = MethodMapper.empty()
        mapper.addMethods(listOf(SampleEchoMethod(), AdderMethod()))

        val methodName = "adder"
        val params = listOf(1, 2)
        val reqAdder = Request.of(methodName, params)

        assertNotNull(reqAdder)

        val method = mapper.matcher(reqAdder)

        assertNotNull(method)

        val response = method.handle(reqAdder, params)

        assertNotNull(response)
        assertTrue(response.isSuccess())

        val successInfo = response.getSuccessInfo()

        assertNotNull(successInfo)
        assertEquals(3, successInfo)

        val paramsEcho = "hello, world!"
        val reqEcho = RequestImpl.of("echo", paramsEcho)

        assertNotNull(reqEcho)

        val methodEcho = mapper.matcher(reqEcho)

        assertNotNull(methodEcho)

        val responseEcho = methodEcho.handle(reqEcho, paramsEcho)

        assertNotNull(responseEcho)
        assertTrue(responseEcho.isSuccess())
        assertNotNull(responseEcho.getSuccessInfo())

        val result = responseEcho.getSuccessInfo() as Map<*, *>

        assertEquals("echo", result["method"])
        assertEquals(paramsEcho, result["params"])
    }

    @Test
    fun addMethodTest() {
        val mapper = MethodMapper.from(AdderMethod())
        mapper.addMethod(SampleEchoMethod())

        val paramsEcho = "hello, world!"
        val reqEcho = RequestImpl.of("echo", paramsEcho)

        assertNotNull(reqEcho)

        val methodEcho = mapper.matcher(reqEcho)

        assertNotNull(methodEcho)

        val responseEcho = methodEcho.handle(reqEcho, paramsEcho)

        assertNotNull(responseEcho)
        assertTrue(responseEcho.isSuccess())
        assertNotNull(responseEcho.getSuccessInfo())

        val result = responseEcho.getSuccessInfo() as Map<*, *>

        assertEquals("echo", result["method"])
        assertEquals(paramsEcho, result["params"])
    }

    @Test
    fun setMethodTest() {
        val mapper = MethodMapper.empty()
        mapper.setMethod(listOf(SampleEchoMethod(), AdderMethod()))


        val methodName = "adder"
        val params = listOf(1, 2)
        val reqAdder = Request.of(methodName, params)

        assertNotNull(reqAdder)

        val method = mapper.matcher(reqAdder)

        assertNotNull(method)

        val response = method.handle(reqAdder, params)

        assertNotNull(response)
        assertTrue(response.isSuccess())

        val successInfo = response.getSuccessInfo()

        assertNotNull(successInfo)
        assertEquals(3, successInfo)
    }

    @Test
    fun customMatcherTest() {
        val mapper = MethodMapper.from { req ->
            if (req.getMethodName().startsWith("add")) {
                AdderMethod()
            } else {
                SampleEchoMethod()
            }
        }

        val methodName = "adder"
        val params = listOf(1, 2)
        val reqAdder = Request.of(methodName, params)

        assertNotNull(reqAdder)

        val method = mapper.matcher(reqAdder)

        assertNotNull(method)

        val response = method.handle(reqAdder, params)

        assertNotNull(response)
        assertTrue(response.isSuccess())

        val successInfo = response.getSuccessInfo()

        assertNotNull(successInfo)
        assertEquals(3, successInfo)

        val paramsEcho = "hello, world!"
        val reqEcho = RequestImpl.of("echo", paramsEcho)

        assertNotNull(reqEcho)

        val methodEcho = mapper.matcher(reqEcho)

        assertNotNull(methodEcho)

        val responseEcho = methodEcho.handle(reqEcho, paramsEcho)

        assertNotNull(responseEcho)
        assertTrue(responseEcho.isSuccess())
        assertNotNull(responseEcho.getSuccessInfo())

        val result = responseEcho.getSuccessInfo() as Map<*, *>

        assertEquals("echo", result["method"])
        assertEquals(paramsEcho, result["params"])
    }

@Test
fun fromCollectionTest() {
    val mapper = MethodMapper.from(listOf(SampleEchoMethod(), AdderMethod()))

    val methodName = "adder"
    val params = listOf(1, 2)
    val reqAdder = Request.of(methodName, params)

    assertNotNull(reqAdder)

    val method = mapper.matcher(reqAdder)

    assertNotNull(method)

    val response = method.handle(reqAdder, params)

    assertNotNull(response)
    assertTrue(response.isSuccess())

    val successInfo = response.getSuccessInfo()

    assertNotNull(successInfo)
    assertEquals(3, successInfo)

    val reqAddrInvalid = Request.of(methodName, null)

    assertNotNull(reqAddrInvalid)

    val methodInvalid = mapper.matcher(reqAddrInvalid)

    assertNotNull(methodInvalid)

    val responseInvalid = methodInvalid.handle(reqAddrInvalid, 3)

    assertNotNull(responseInvalid)
    assertFalse(responseInvalid.isSuccess())
    assertEquals(PresetError.INVALID_PARAMS.code, responseInvalid.getErrorInfo()?.code)
    assertEquals(PresetError.INVALID_PARAMS.message, responseInvalid.getErrorInfo()?.message)

    val paramsEcho = "hello, world!"
    val reqEcho = RequestImpl.of("echo", paramsEcho)

    assertNotNull(reqEcho)

    val methodEcho = mapper.matcher(reqEcho)

    assertNotNull(methodEcho)

    val responseEcho = methodEcho.handle(reqEcho, paramsEcho)

    assertNotNull(responseEcho)
    assertTrue(responseEcho.isSuccess())
    assertNotNull(responseEcho.getSuccessInfo())

    val result = responseEcho.getSuccessInfo() as Map<*, *>

    assertEquals("echo", result["method"])
    assertEquals(paramsEcho, result["params"])
}

@Test
fun fromVarargsTest() {
    val mapper = MethodMapper.from(AdderMethod(), SampleEchoMethod())

    val methodName = "adder"
    val params = listOf(1, 2)
    val reqAdder = Request.of(methodName, params)

    assertNotNull(reqAdder)

    val method = mapper.matcher(reqAdder)

    assertNotNull(method)

    val response = method.handle(reqAdder, params)

    assertNotNull(response)
    assertTrue(response.isSuccess())

    val successInfo = response.getSuccessInfo()

    assertNotNull(successInfo)
    assertEquals(3, successInfo)

    val reqAddrInvalid = Request.of(methodName, null)

    assertNotNull(reqAddrInvalid)

    val methodInvalid = mapper.matcher(reqAddrInvalid)

    assertNotNull(methodInvalid)

    val responseInvalid = methodInvalid.handle(reqAddrInvalid, 3)

    assertNotNull(responseInvalid)
    assertFalse(responseInvalid.isSuccess())
    assertEquals(PresetError.INVALID_PARAMS.code, responseInvalid.getErrorInfo()?.code)
    assertEquals(PresetError.INVALID_PARAMS.message, responseInvalid.getErrorInfo()?.message)

    val paramsEcho = "hello, world!"
    val reqEcho = RequestImpl.of("echo", paramsEcho)

    assertNotNull(reqEcho)

    val methodEcho = mapper.matcher(reqEcho)

    assertNotNull(methodEcho)

    val responseEcho = methodEcho.handle(reqEcho, paramsEcho)

    assertNotNull(responseEcho)
    assertTrue(responseEcho.isSuccess())
    assertNotNull(responseEcho.getSuccessInfo())

    val result = responseEcho.getSuccessInfo() as Map<*, *>

    assertEquals("echo", result["method"])
    assertEquals(paramsEcho, result["params"])
}
}