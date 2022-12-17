package dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.type.TypeReference
import dispatcher.JacksonParser
import dispatcher.JsonParser

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes(
    JsonSubTypes.Type(value = ResponseSuccess::class),
    JsonSubTypes.Type(value = ResponseError::class)
)
interface Response {

    fun getVersion(): String

    fun getResponseId(): String?

    fun isSuccess(): Boolean

    fun getSuccessJson(): String?

    fun getSuccessInfo(): Any?

    fun getErrorInfo(): ErrorField<Any>?

    fun toJson(): String?

    companion object {

        fun fromJson(json: String, parser: JsonParser = JacksonParser): Response {
            return ResponseJsonHolder(json, parser = parser)
        }

        fun <T> success(result: T, request: Request? = null, parser: JsonParser = JacksonParser): Response {
            return ResponseSuccess(
                result,
                request?.getVersion() ?: "2.0",
                request?.getRequestId() ?: "0",
                parser = parser
            )
        }

        fun error(
            errorCode: ErrorCode,
            id: String? = "0",
            jsonrpc: String = "2.0",
            parser: JsonParser = JacksonParser
        ): Response {
            return ResponseError(ErrorField.from(errorCode), jsonrpc, id, parser)
        }
    }
}

class ResponseJsonHolder(private val json: String, private val parser: JsonParser = JacksonParser) : Response {

    @JsonIgnore
    override fun getVersion(): String = try {
        parser.readTree(json)
            ?.findValue("jsonrpc")
            ?.asText() ?: "2.0"
    } catch (e: Exception) {
        "2.0"
    }

    @JsonIgnore
    override fun getResponseId(): String? = try {
        parser.readTree(json)
            ?.findValue("id")
            ?.asText()
    } catch (e: Exception) {
        null
    }

    @JsonIgnore
    override fun isSuccess(): Boolean = try {
        parser.readTree(json)
            ?.findValue("result")
            ?.let { !it.isNull() }
            ?: false
    } catch (e: Exception) {
        false
    }

    @JsonIgnore
    override fun getSuccessJson(): String? = try {
        parser.readTree(json)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }?.findValue("result")?.toString()

    @JsonIgnore
    override fun getSuccessInfo(): Any? = try {
        parser.readTree(json)
            ?.findValue("result")
            ?.let {
                parser.deserialize(it.toString(), object : TypeReference<Any>() {})
            }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    @JsonIgnore
    override fun getErrorInfo(): ErrorField<Any>? = try {
        parser.readTree(json)
            ?.findValue("error")
            ?.let {
                parser.deserialize(it.toString(), object : TypeReference<ErrorField<Any>>() {})
            }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    @JsonIgnore
    override fun toJson(): String = json

    override fun toString() = toJson()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseJsonHolder

        if (json != other.json) return false

        return true
    }

    override fun hashCode(): Int {
        return json.hashCode()
    }

}

class ResponseSuccess<T>(
    val result: T,
    val jsonrpc: String = "2.0",
    val id: String? = "0",
    private val parser: JsonParser = JacksonParser
) : Response {

    @JsonIgnore
    override fun getVersion() = jsonrpc

    @JsonIgnore
    override fun getResponseId() = id

    @JsonIgnore
    override fun isSuccess() = true

    @JsonIgnore
    override fun getSuccessJson() = parser.serialize(result)

    @JsonIgnore
    override fun getSuccessInfo() = result

    @JsonIgnore
    override fun getErrorInfo() = null

    @JsonIgnore
    override fun toJson() = parser.serialize(this)

    override fun toString(): String {
        return "ResponseSuccess(result=$result, jsonrpc='$jsonrpc', id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseSuccess<*>

        if (result != other.result) return false
        if (jsonrpc != other.jsonrpc) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = result?.hashCode() ?: 0
        result1 = 31 * result1 + jsonrpc.hashCode()
        result1 = 31 * result1 + (id?.hashCode() ?: 0)
        return result1
    }

}

/**
 * This data class is used to represent the Error object in the JSON-RPC 2.0 specification.
 * In a failure response, the data field may be omitted and is allowed to be defined by the server.
 * @param T type of error `data` field
 */
data class ErrorField<out T>(override val code: Int, override val message: String, val data: T?) : ErrorCode {
    companion object {
        /**
         * Returns a new instance of [ErrorField] with the [code] and [message] from the [errorCode].
         */
        fun from(errorCode: ErrorCode) = ErrorField(errorCode.code, errorCode.message, null)
    }
}

class ResponseError<T : Any>(
    val error: ErrorField<T>,
    val jsonrpc: String = "2.0",
    val id: String? = "0",
    private val parser: JsonParser = JacksonParser
) : Response {

    @JsonIgnore
    override fun getVersion() = jsonrpc

    @JsonIgnore
    override fun getResponseId() = id

    @JsonIgnore
    override fun isSuccess() = false

    @JsonIgnore
    override fun getSuccessJson() = null

    @JsonIgnore
    override fun getSuccessInfo() = null

    @JsonIgnore
    override fun getErrorInfo(): ErrorField<Any> = error

    @JsonIgnore
    override fun toJson(): String? = parser.serialize(this)

    override fun toString(): String {
        return "ResponseError(error=$error, jsonrpc='$jsonrpc', id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseError<*>

        if (error != other.error) return false
        if (jsonrpc != other.jsonrpc) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = error.hashCode()
        result = 31 * result + jsonrpc.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

}