package jsonrpc.dto

import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

/**
 * It is a default implementation of [ContextHolder]. It uses a thread-safe [BlockingDeque] to store the context.
 */
class RpcContext private constructor(
    private val requests: BlockingDeque<Request>,
    private val responses: BlockingDeque<Response>,
    private val isDone: Boolean,
    private val isBatch: Boolean
) : ContextHolder {

    companion object {

        /**
         * Returns a new instance of [RpcContext] using the given parameters.
         */
        fun of(isBatch: Boolean, requests: List<Request>, responses: List<Response>): ContextHolder {
            return RpcContext(LinkedBlockingDeque(requests), LinkedBlockingDeque(responses), false, isBatch)
        }

        /**
         * Returns a new instance of [RpcContext] using the given parameters. A requests property is empty.
         */
        fun of(isBatch: Boolean, vararg responses: Response): ContextHolder {
            return RpcContext(LinkedBlockingDeque(), LinkedBlockingDeque(responses.toList()), false, isBatch)
        }
    }

    override fun getRequests() = requests.toList()

    override fun getResponses() = responses.toList()

    override fun isDone() = isDone

    override fun done(responses: Collection<Response>?) =
        responses?.let {
            RpcContext(requests, LinkedBlockingDeque(responses), true, isBatch)
        } ?: RpcContext(requests, this.responses, true, isBatch)

    override fun isBatch() = isBatch

    override fun toString(): String {
        return "RpcContext(requests=$requests, responses=$responses, isDone=$isDone, isBatch=$isBatch)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RpcContext

        if (requests != other.requests) return false
        if (responses != other.responses) return false
        if (isDone != other.isDone) return false
        if (isBatch != other.isBatch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requests.hashCode()
        result = 31 * result + responses.hashCode()
        result = 31 * result + isDone.hashCode()
        result = 31 * result + isBatch.hashCode()
        return result
    }

}