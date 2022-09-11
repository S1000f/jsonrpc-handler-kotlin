package dispatcher

import com.fasterxml.jackson.databind.JsonNode

class JacksonParser(private val node: JsonNode) : JsonHolder {

    override fun iterator(): Iterator<JsonHolder> {
        TODO("Not yet implemented")
    }

    override fun isArray(): Boolean {
        return node.isArray
    }

    override fun isEmpty(): Boolean {
        return node.isEmpty
    }

    override fun findValue(fieldName: String): JsonHolder? {
        return node.findValue(fieldName)
    }

    override fun isIntegralNumber(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTextual(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isNull(): Boolean {
        TODO("Not yet implemented")
    }

    override fun asText(): String {
        TODO("Not yet implemented")
    }

    override fun textValue(): String? {
        TODO("Not yet implemented")
    }
}