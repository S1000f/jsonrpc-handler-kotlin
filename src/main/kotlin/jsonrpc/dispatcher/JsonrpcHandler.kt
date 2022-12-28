package jsonrpc.dispatcher

import jsonrpc.dto.Request
import jsonrpc.mapper.HandlerMapper
import jsonrpc.method.RpcMethod

/**
 * It is a default implementation of [Dispatcher]. It inherits [AbstractDispatcher] using String as the type of the
 * JSON-RPC request and the JSON-RPC response. [Specification] is used to validate the request and the response, while
 * you can replace it with your own implementation.
 *
 * This class uses [HandlerMapper] to find [RpcMethod] which is matched with the given request and then, it delegates
 * the handling to the method.
 */
class JsonrpcHandler(
    handlerMapper: HandlerMapper,
    contextBuilder: ContextBuilder = Specification.contextBuilder(),
    parser: JsonParser = JacksonParser
) : AbstractDispatcher(contextBuilder, handlerMapper, parser) {

    override fun handle(method: RpcMethod, request: Request, params: Any?) = method.handle(request, params)
}