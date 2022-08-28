import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Request(val jsonrpc: String, val method: String, val id: String?, @Contextual val params: Any?)