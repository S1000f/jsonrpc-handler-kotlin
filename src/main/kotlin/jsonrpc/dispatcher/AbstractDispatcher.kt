package jsonrpc.dispatcher

import com.fasterxml.jackson.core.type.TypeReference
import jsonrpc.dto.ContextHolder
import jsonrpc.dto.PresetError
import jsonrpc.dto.Request
import jsonrpc.dto.Response
import jsonrpc.mapper.HandlerMapper
import jsonrpc.method.RpcMethod
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Abstract dispatcher for handling requests. It implements the main logic which is template method and provides
 * hook method, [marshal] for extending. [JacksonParser] is used as default parser.
 *
 * This abstract class uses the marshal method to format the result of the handle method.
 * This ensures that the output follows the JSON-RPC specification.
 */
abstract class AbstractDispatcher(
    override val contextBuilder: ContextBuilder,
    val handlerMapper: HandlerMapper? = null,
    override val parser: JsonParser = JacksonParser
) : Dispatcher<String, String> {

    /**
     * Returns a JSON-RPC response. The argument must be marked as done which means [ContextHolder.isDone] returns true.
     * It also checks if the request is a batch. If it is, this method returns a JSON-RPC response which is a batch.
     */
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

    /**
     * Returns [Response] which is built from a matched [RpcMethod]. It may be overridden to change the behavior before
     * returning a response that [RpcMethod] has produced.
     */
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