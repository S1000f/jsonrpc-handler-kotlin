package dispatcher

/**
 * It contains the versions of the JSON-RPC specification. The Notification feature is in the spec. The requirement of
 * the feature may be different in each version. Therefore, implementations of this interface must clarify whether a request
 * is a Notification or a Request.
 */
interface Version {

    /**
     * Returns the version of the JSON-RPC specification, e.g. "2.0".
     */
    val version: String

    /**
     * Returns true if the request is a Notification, false otherwise.
     */
    fun isNotification(jsonrpc: String, id: String?): Boolean

}