package method

import com.fasterxml.jackson.core.type.TypeReference
import dto.Request
import dto.Response
import java.time.Instant

class SampleEchoMethod : RpcMethod {
    override fun getName(): String = "echo"

    override fun getParamsType(): TypeReference<*>? = null

    override fun handle(request: Request, params: Any): Response =
        mapOf(
            "method" to request.getMethodName(),
            "params" to params,
            "timestamp" to Instant.now().epochSecond
        ).let { map -> Response.success(map, request) }

}