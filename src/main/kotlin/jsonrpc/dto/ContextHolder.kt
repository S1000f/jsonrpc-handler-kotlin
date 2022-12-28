package jsonrpc.dto

/**
 * This [ContextHolder] contains requests and responses that occur during the request-response cycle.
 * It must exist throughout the cycle, regardless of the validity of the request, even if all requests are
 * Notification ones.
 *
 * The implementation of this [ContextHolder] must be immutable throughout its lifecycle.
 * @see Request
 * @see Response
 */
interface ContextHolder {

    /**
     * Returns a read-only list of requests which are contained in this context.
     * If the request of this thread is a batch request, the list will contain all requests in the batch.
     */
    fun getRequests(): List<Request>

    /**
     * Returns a read-only list of responses which are contained in this context.
     * The list may contain both a successful response and a failed response.
     * If all requests are of the Notification type, the list will be empty, but it will never be null.
     */
    fun getResponses(): List<Response>

    /**
     * Returns true if the context is done, false otherwise. If the context is done, it means that the response
     * has been prepared and is ready to be sent. However, this does not mean that the response has been sent to the client
     */
    fun isDone(): Boolean

    /**
     * Returns a new [ContextHolder] instance which is marked as done. If the given response list is not null,
     * the response list in the new [ContextHolder] instance will be replaced with the given response list.
     */
    fun done(responses: Collection<Response>? = null): ContextHolder

    /**
     * Returns true if the request is a batch request, false otherwise. There is no definition of the number of requests
     * in a batch request. Whether a request is considered a batch request depends on whether its JSON representation
     * is an array or not.
     */
    fun isBatch(): Boolean
}