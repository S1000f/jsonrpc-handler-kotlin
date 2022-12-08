package method

import com.fasterxml.jackson.core.type.TypeReference
import dto.Request
import dto.Response

abstract class AbstractRouteMethod(private val name: String, val endpoint: String) :RpcMethod {

    protected abstract fun route(endpoint: String, request: Request, params: Any?): String?

    override fun getName() = name

    override fun getParamsType(): TypeReference<*>? = null

    override fun handle(request: Request, params: Any) = route(this.endpoint, request, params)
        ?.let { json -> Response.fromJson(json) }
}