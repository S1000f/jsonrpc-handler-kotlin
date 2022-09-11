package dispatcher

interface JsonParser {
    fun readTree(json: String): JsonHolder?
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