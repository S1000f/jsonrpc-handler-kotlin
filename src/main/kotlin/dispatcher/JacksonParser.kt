package dispatcher

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import util.Helpers

class JacksonParser private constructor(private val node: JsonNode) : JsonHolder {

    companion object : JsonParser {
        override fun readTree(json: String?) = json?.let { JacksonParser(Helpers.getMapper().readTree(it)) }

        override fun <T> serialize(data: T): String? = try {
            Helpers.getMapper().writeValueAsString(data)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            null
        }

        override fun <T> deserialize(json: String, type: TypeReference<T>): T? = try {
            Helpers.getMapper().readValue(json, type)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            null
        }
    }

    override fun iterator() = node.iterator()
        .asSequence()
        .map { JacksonParser(it) }
        .iterator()

    override fun isArray() = node.isArray

    override fun isEmpty() = node.isEmpty

    override fun findValue(fieldName: String) = node.findValue(fieldName)?.let { JacksonParser(it) }

    override fun isIntegralNumber() = node.isIntegralNumber

    override fun isTextual() = node.isTextual

    override fun isNull() = node.isNull

    override fun asText(): String = node.asText()

    override fun textValue(): String? = node.textValue()

    override fun toString() = node.toString()
}
