package dispatcher

import com.fasterxml.jackson.databind.JsonNode
import util.getMapper

class JacksonParser(private val node: JsonNode) : JsonHolder {

    companion object : JsonParser {
        override fun readTree(json: String): JsonHolder? {
            val readTree = getMapper().readTree(json) ?: return null
            return JacksonParser(readTree)
        }
    }

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
        TODO()
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