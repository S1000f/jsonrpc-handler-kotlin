package method

import com.fasterxml.jackson.core.type.TypeReference
import dto.Request
import dto.Response

class HttpRouteMethod : RpcMethod {

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getParamsType(): TypeReference<*>? {
        TODO("Not yet implemented")
    }

    override fun handle(request: Request, params: Any): Response? {
        TODO("Not yet implemented")
    }
}