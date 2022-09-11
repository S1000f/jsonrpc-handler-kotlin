package method

import com.fasterxml.jackson.core.type.TypeReference
import dto.Request
import dto.Response

interface RpcMethod {

    fun getName(): String

    fun getParamsType(): TypeReference<*>?

    fun handle(request: Request, params: Any): Response?
}