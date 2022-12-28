package jsonrpc.method

import com.fasterxml.jackson.core.type.TypeReference
import jsonrpc.dto.Request
import jsonrpc.dto.Response
import java.time.Instant

class SampleEchoMethod : RpcMethod {
    override fun getName(): String = "echo"

    override fun getParamsType(): TypeReference<*>? = null

    override fun handle(request: Request, params: Any?): Response {
        mapOf(
            "method" to request.getMethodName(),
            "timestamp" to Instant.now().epochSecond
        ).let { map -> return Response.success(map, request) }
    }

}