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
        fun of(isBatch: Boolean, requests: List<Request>, responses: List<Response>): ContextHolder {
            return RpcContext(LinkedBlockingDeque(requests), LinkedBlockingDeque(responses), false, isBatch)
        }

        fun of(isBatch: Boolean, vararg responses: Response): ContextHolder {
            return RpcContext(LinkedBlockingDeque(), LinkedBlockingDeque(responses.toList()), false, isBatch)
        }
    }

    override fun getRequests() = requests.toList()

    override fun getResponses() = responses.toList()

    override fun isDone() = isDone

    override fun done() = RpcContext(requests, responses, true, isBatch)

    override fun isBatch() = isBatch

}