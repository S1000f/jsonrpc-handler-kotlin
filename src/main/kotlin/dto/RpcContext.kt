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
    }



    override fun getRequests(): List<Request> {
        TODO("Not yet implemented")
    }

    override fun getResponses(): List<Response> {
        TODO("Not yet implemented")
    }

    override fun isDone(): Boolean {
        TODO("Not yet implemented")
    }

    override fun done(): ContextHolder {
        TODO("Not yet implemented")
    }

    override fun isBatch(): Boolean {
        TODO("Not yet implemented")
    }
}