package dto

interface ContextHolder {

    fun getRequests(): List<Request>

    fun getResponses(): List<Response>

    fun isDone(): Boolean

    fun done(): ContextHolder

    fun isBatch(): Boolean
}