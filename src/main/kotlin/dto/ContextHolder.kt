package dto

interface ContextHolder {

    fun getRequests(): List<Request>

    fun getResponses(): List<Response>

    fun isDone(): Boolean

    fun done(responses: Collection<Response>? = null): ContextHolder

    fun isBatch(): Boolean
}