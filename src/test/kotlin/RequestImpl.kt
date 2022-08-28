import kotlinx.serialization.Serializable

@Serializable
data class RequestImpl<T>(val jsonrpc: String, val method: String, val id: String, val params: T?) {
}