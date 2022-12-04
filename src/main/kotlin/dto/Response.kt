package dto

interface Response {

    val jsonrpc: String

    val id: String

    fun isSuccess(): Boolean

    fun getSuccessInfo(): Any?

    companion object {
        fun error(jsonrpc: String, errorCode: ErrorCode): Response {
            return ResponseError.of(jsonrpc, errorCode)
        }
    }

}

class ErrorField<out T>(val code: Int, val message: String, val data: T?)
fun Response.getErrorInfo(): ErrorField<Any>? = null

data class ResponseError(override val jsonrpc: String, override val id: String, private val error: ErrorField<Any>?) : Response {

    companion object {
        fun of(jsonrpc: String, errorCode: ErrorCode): ResponseError {
            return ResponseError(jsonrpc, "0", ErrorField(errorCode.code, errorCode.message, null))
        }
    }

    override fun isSuccess(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSuccessInfo(): Any? {
        TODO("Not yet implemented")
    }

}