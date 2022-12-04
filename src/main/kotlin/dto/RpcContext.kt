package dto

import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class RpcContext private constructor(
    private val requests: BlockingDeque<Request>,
    private val responses: BlockingDeque<Response>,
    private val isDone: Boolean,
    private val isBatch: Boolean
) : ContextHolder {

    companion object {
        fun of(isBatch: Boolean, requests: List<Request>, responses: List<Response>): RpcContext {
            return RpcContext(LinkedBlockingDeque(requests), LinkedBlockingDeque(responses), false, isBatch)
        }

        fun of(isBatch: Boolean, vararg responses: Response): RpcContext {
            return RpcContext(LinkedBlockingDeque(), LinkedBlockingDeque(responses.toList()), false, isBatch)
        }
    }

    override fun getRequests(): List<Request> {
        return requests.toList()
    }

    override fun getResponses(): List<Response> {
        return responses.toList()
    }

    override fun isDone(): Boolean {
        return isDone
    }

    override fun done(): ContextHolder {
        return RpcContext(requests, responses, true, isBatch)
    }

    override fun isBatch(): Boolean {
        return isBatch
    }
}