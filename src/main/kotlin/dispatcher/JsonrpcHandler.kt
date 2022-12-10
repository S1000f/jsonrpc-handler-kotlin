package dispatcher

import dto.Request
import mapper.HandlerMapper
import method.RpcMethod

class JsonrpcHandler(
    handlerMapper: HandlerMapper,
    contextBuilder: ContextBuilder = Specification.contextBuilder(),
    parser: JsonParser = JacksonParser
) :
    AbstractDispatcher(contextBuilder, handlerMapper, parser) {

    override fun handle(method: RpcMethod, request: Request, params: Any?) = method.handle(request, params)
}