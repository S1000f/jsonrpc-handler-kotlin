package handler

import dto.Request
import method.RpcMethod

class MethodMapper private constructor(
    matcher: ((Request) -> RpcMethod?)?,
    methodMap: Map<String, RpcMethod>
) : AbstractHandlerMapper(matcher, methodMap) {

    companion object {
        fun empty(): HandlerMapper = MethodMapper(null, emptyMap())

        fun from(matcher: (Request) -> RpcMethod?): HandlerMapper = MethodMapper(matcher, emptyMap())

        fun from(methods: Collection<RpcMethod>): HandlerMapper {
            mutableMapOf<String, RpcMethod>().apply {
                methods.forEach { method ->
                    putIfAbsent(method.getName(), method)
                }
            }.let { methodMap ->
                return MethodMapper(null, methodMap)
            }
        }

        fun from(vararg methods: RpcMethod): HandlerMapper = from(methods.toList())
    }

}