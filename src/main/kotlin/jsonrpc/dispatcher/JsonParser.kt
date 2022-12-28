package jsonrpc.dispatcher

import com.fasterxml.jackson.core.type.TypeReference

interface JsonParser {

    fun readTree(json: String?): JsonHolder?

    fun <T> serialize(data: T): String?

    fun <T> deserialize(json: String, type: TypeReference<T>): T?

}

interface JsonHolder : Iterable<JsonHolder> {

    fun isArray(): Boolean

    fun isEmpty(): Boolean

    fun findValue(fieldName: String): JsonHolder?

    fun isIntegralNumber(): Boolean

    fun isTextual(): Boolean

    fun isNull(): Boolean

    fun asText(): String

    fun textValue(): String?
}