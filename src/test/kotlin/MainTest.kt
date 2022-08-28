import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {

    private val jsonWithIdParamsArray = """
        {"jsonrpc":"2.0","method":"echo","id":"1","params":[1,2]}
    """.trimIndent()

    private val jsonWithIdNoparams = """
        {"jsonrpc":"2.0","method":"echo","id":"1"}
    """.trimIndent()

    @Test
    fun serializeTest() {
        val requestImpl = RequestImpl("2.0", "echo", "1", listOf(1, 2, 3))

        println(requestImpl)

        Json.encodeToString(requestImpl)
    }

    @Test
    fun deserializeTest() {
        Json.decodeFromString<RequestImpl<Any?>>(jsonWithIdNoparams)
    }

    @Test
    fun jacksonMapperTest() {
        val mapper = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)

        val req = mapper.readValue(jsonWithIdParamsArray, RequestImpl::class.java)
        val req1 = mapper.readValue(jsonWithIdNoparams, RequestImpl::class.java)

        println(req)
        println(req1)
    }

    @Test
    fun typeReferenceTest() {
        val mapper = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)

        val req = mapper.readValue(jsonWithIdParamsArray, RequestImpl::class.java)
        val req1 = mapper.readValue(jsonWithIdParamsArray, Request::class.java)

        println(req)
        println(req1)
    }

    @Test
    fun customParamsClass() {
        val json = """
            {"jsonrpc":"2.0","method":"echo","id":"1","params":{"x":42,"y":"hello"}}
        """.trimIndent()

        val mapper = jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)

        val readTree = mapper.readTree(json)
        val findParams = readTree.findValue("params")

        val clazz = ParamOne::class.java

        val readValue = mapper.readValue(findParams.toString(), getParamsClass())

        val paramOne = readValue as ParamOne



    }

    fun getParamsClass(): Class<*> {
        return ParamOne::class.java
    }

}