package dto

import com.fasterxml.jackson.annotation.JsonIgnore
import dispatcher.JacksonParser
import dispatcher.JsonParser
import kotlinx.serialization.Serializable

/**
 * It is a request object that is sent to the server. Here is an example of a request:
 * ```json
 * {"jsonrpc": "2.0", "method": "subtract", "params": [42, 41], "id": "0"}
 * ```
 * for more information, see [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification)
 */
interface Request {

    /**
     * Returns a JSON-RPC version.
     */
    fun getVersion(): String

    /**
     * Returns a method name.
     */
    fun getMethodName(): String

    /**
     * Returns true if the request is a Notification.
     */
    fun isNotification(): Boolean

    /**
     * Returns a parameter in JSON format. The parameters are not mandatory.
     */
    fun getParameters(): String?

    /**
     * Returns an id of the request. The id can be string, integer or null. This method always returns a string converting
     * the integer to string.
     *
     * Returning null does not guarantee that the request is a Notification. If you want to check it, use [isNotification] method.
     */
    fun getRequestId(): String?

    /**
     * Returns a JSON string of the request.
     */
    fun toJson(): String?

    companion object {
        fun <T> of(method: String, params: T?, jsonrpc: String = "2.0", id: String? = "0"): Request? =
            RequestImpl.of(method, params, jsonrpc, id)
    }
}

/**
 * This data class is a default implementation of [Request]. It keeps a json string of the request.
 */
data class RequestImpl(
    private val method: String,
    private val notificator: Boolean,
    private val json: String,
    private val params: String?,
    private val jsonrpc: String = "2.0",
    private val id: String? = "0",
) : Request {

    companion object {

        /**
         * Returns a new instance of [RequestImpl] using the given parameters. If the given parameters are invalid, it
         * returns null.
         *
         * It creates a json string of the request using [parser].
         */
        fun <T> of(
            method: String,
            params: T?,
            jsonrpc: String = "2.0",
            id: String? = "0",
            parser: JsonParser = JacksonParser
        ): Request? {
            if (method.isEmpty()) {
                return null
            }

            val serialized = Scaffold(method, params, jsonrpc, id, parser)
                .let {
                    parser.serialize(it)
                } ?: return null

            val jsonParams = params?.let {
                parser.serialize(it)
            }

            // FIXME: apply encapsulation
            val isNotifying = !(jsonrpc == "2.0" && !id.isNullOrEmpty())

            return RequestImpl(method, isNotifying, serialized, jsonParams, jsonrpc, id)
        }
    }

    override fun getVersion() = jsonrpc

    override fun getMethodName() = method

    override fun isNotification() = notificator

    override fun getParameters() = params

    override fun getRequestId() = id

    override fun toJson() = json

    override fun toString() = json

}

/**
 * This class is a default implementation of [Request] using [Serializable] annotation.
 * @param T the type of the parameters
 */
@Serializable
class Scaffold<T>(
    val method: String,
    val params: T?,
    val jsonrpc: String = "2.0",
    val id: String? = "0",
    private val parser: JsonParser = JacksonParser
) : Request {

    @JsonIgnore
    override fun getVersion() = jsonrpc

    @JsonIgnore
    override fun getMethodName() = method

    // FIXME: apply encapsulation
    @JsonIgnore
    override fun isNotification() = jsonrpc == "2.0" && id == null

    @JsonIgnore
    override fun getParameters() = params?.let { parser.serialize(it) }

    @JsonIgnore
    override fun getRequestId() = id

    @JsonIgnore
    override fun toJson() = parser.serialize(this)

    override fun toString(): String {
        return "Scaffold(method='$method', params=$params, jsonrpc='$jsonrpc', id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scaffold<*>

        if (method != other.method) return false
        if (params != other.params) return false
        if (jsonrpc != other.jsonrpc) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + (params?.hashCode() ?: 0)
        result = 31 * result + jsonrpc.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }

}