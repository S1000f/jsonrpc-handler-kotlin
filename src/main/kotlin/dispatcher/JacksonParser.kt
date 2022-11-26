package dispatcher

import com.fasterxml.jackson.databind.JsonNode
import util.Helpers

class JacksonParser private constructor(private val node: JsonNode) : JsonHolder {

    companion object : JsonParser {
        override fun readTree(json: String): JsonHolder {
            return JacksonParser(Helpers.getMapper().readTree(json))
        }
    }

    override fun iterator(): Iterator<JsonHolder> {
        val iterator = node.iterator()

        return iterator.asSequence()
            .map { JacksonParser(it) }
            .iterator()
    }

    override fun isArray(): Boolean {
        return node.isArray
    }

    override fun isEmpty(): Boolean {
        return node.isEmpty
    }

    override fun findValue(fieldName: String): JsonHolder? {
        val jsonNode = node.findValue(fieldName) ?: return null
        return JacksonParser(jsonNode)
    }

    override fun isIntegralNumber(): Boolean {
        return node.isIntegralNumber
    }

    override fun isTextual(): Boolean {
        return node.isTextual
    }

    override fun isNull(): Boolean {
        return node.isNull
    }

    override fun asText(): String {
        return node.asText()
    }

    override fun textValue(): String? {
        return node.textValue()
    }

    override fun toString(): String {
        return node.toString()
    }
}
