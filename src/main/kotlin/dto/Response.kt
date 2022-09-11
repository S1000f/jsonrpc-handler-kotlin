package dto

interface Response {

    val isSuccess: Boolean?

    fun getSuccessInfo(): Any?

    companion object class ErrorField<out T>(val code: Int, val message: String, val data: T)
}

fun Response.getErrorInfo(): Response.ErrorField<Any>? = null