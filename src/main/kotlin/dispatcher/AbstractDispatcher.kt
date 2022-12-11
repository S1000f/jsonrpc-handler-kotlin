package dispatcher

import com.fasterxml.jackson.core.type.TypeReference
import dto.ContextHolder
import dto.PresetError
import dto.Request
import dto.Response
import mapper.HandlerMapper
import method.RpcMethod
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

abstract class AbstractDispatcher(
    override val contextBuilder: ContextBuilder,
    val handlerMapper: HandlerMapper? = null,
    override val parser: JsonParser = JacksonParser
) : Dispatcher<String, String> {

    protected fun marshal(contextHolder: ContextHolder): String? {
        val responses = contextHolder.getResponses()

        if (!contextHolder.isDone() || responses.isEmpty()) {
            return null
        }

        if (!contextHolder.isBatch() && responses.size == 1) {
            return responses.first().toJson()
        }

        buildString {
            append("[")
            responses.forEachIndexed { index, response ->
                append(response.toJson())
                if (index < responses.size - 1) {
                    append(",")
                }
            }
            append("]")
        }.let { return it }
    }

    protected abstract fun handle(method: RpcMethod, request: Request, params: Any?): Response?

    override fun match(request: Request): RpcMethod? = handlerMapper?.let { it.matcher(request) }

    override fun dispatch(jsonPayload: String?): String? {
        if (jsonPayload.isNullOrEmpty()) {
            return Response.error(PresetError.INTERNAL_ERROR).toJson()
        }

        val jsonNode = try {
            parser.readTree(jsonPayload)
        } catch (e: Exception) {
            return Response.error(PresetError.PARSE_ERROR).toJson()
        } ?: return Response.error(PresetError.PARSE_ERROR).toJson()

        val contextHolder = build(jsonNode) ?: return Response.error(PresetError.INVALID_REQUEST).toJson()

        if (contextHolder.isDone()) {
            return marshal(contextHolder)
        }

        val resolved: ConcurrentMap<Request, Response> = ConcurrentHashMap()

        for (req in contextHolder.getRequests()) {
            val requestId = req.getRequestId()
            val method = match(req)

            if (method == null) {
                resolved[req] = Response.error(PresetError.METHOD_NOT_FOUND, requestId)
                continue
            }

            val parameters = req.getParameters()
            val paramsType = method.getParamsType()
            var params: Any? = null

            if (parameters != null && paramsType != null) {
                val deserialize = parser.deserialize(parameters, paramsType)

                if (deserialize == null) {
                    resolved[req] = Response.error(PresetError.INVALID_PARAMS, requestId)
                    continue
                }

                params = deserialize

            } else if (parameters != null) {
                params = parser.deserialize(parameters, object : TypeReference<Map<String, Any>>() {}) ?: parameters
            }

            val response = handle(method, req, params)

            if (req.isNotification()) {
                continue
            }

            if (response == null) {
                resolved[req] = Response.error(PresetError.INTERNAL_ERROR, requestId)
                continue
            }

            resolved[req] = response
        }

        val responses = resolved.values

        if (responses.isEmpty()) {
            return null
        }

        return marshal(contextHolder.done(responses))
    }
}