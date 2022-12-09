package dto

import com.fasterxml.jackson.annotation.JsonIgnore
import dispatcher.JacksonParser
import dispatcher.JsonParser
import kotlinx.serialization.Serializable

interface Request {
    fun getVersion(): String

    fun getMethodName(): String

    fun isNotification(): Boolean

    fun getParameters(): String?

    fun getRequestId(): String?

    fun toJson(): String?

    companion object {
        fun <T> of(method: String, params: T?, jsonrpc: String = "2.0", id: String? = "0"): Request? =
            RequestImpl.of(method, params, jsonrpc, id)
    }
}

data class RequestImpl(
    private val method: String,
    private val notificator: Boolean,
    private val json: String,
    private val params: String?,
    private val jsonrpc: String = "2.0",
    private val id: String? = "0",
) : Request {

    companion object {
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

@Serializable
data class Scaffold<T>(
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

}