package dto

interface Request {
    val version: String
    val methodName: String
    val isNotification: Boolean

    fun getParamaters(): String?

    fun getRequestId(): String?

    fun toJson(): String
}