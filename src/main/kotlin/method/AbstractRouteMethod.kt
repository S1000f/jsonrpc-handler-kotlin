package method

import com.fasterxml.jackson.core.type.TypeReference
import dto.Request
import dto.Response

/**
 * This is an abstract implementation of [RpcMethod] that routes requests to other handlers.
 */
abstract class AbstractRouteMethod(private val name: String, val endpoint: String) :RpcMethod {

    /**
     * Returns a JSON string that is a response of the request. This method delegates the request to the handler that is
     * located at the [endpoint].
     */
    protected abstract fun route(endpoint: String, request: Request, params: Any?): String?

    override fun getName() = name

    override fun getParamsType(): TypeReference<*>? = null

    override fun handle(request: Request, params: Any?) = route(this.endpoint, request, params)
        ?.let { json -> Response.fromJson(json) }
}