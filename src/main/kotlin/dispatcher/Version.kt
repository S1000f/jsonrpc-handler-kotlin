package dispatcher

interface Version {

    val version: String

    fun isNotification(jsonrpc: String, id: String?): Boolean

}