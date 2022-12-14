package dispatcher

/**
 * It contains the versions of the JSON-RPC specification. The Notification feature is in the spec. The requirement of
 * the feature may be different in each version. Therefore, implementations of this interface must clarify whether a request
 * is a Notification or a Request.
 */
interface Version {

    val version: String

    fun isNotification(jsonrpc: String, id: String?): Boolean

}