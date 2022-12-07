package handler

import dto.Request
import method.HttpRouteMethod
import method.RpcMethod

class RouteMapper private constructor(
    matcher: ((Request) -> RpcMethod?)?,
    methodMap: Map<String, RpcMethod>
) : AbstractHandlerMapper(matcher, methodMap) {

    companion object {
        fun empty(): HandlerMapper = RouteMapper(null, emptyMap())

        fun from(matcher: (Request) -> HttpRouteMethod?): HandlerMapper = RouteMapper(matcher, emptyMap())

        fun from(methods: Collection<RpcMethod>): HandlerMapper {
            mutableMapOf<String, RpcMethod>().apply {
                methods.forEach { method ->
                    putIfAbsent(method.getName(), method)
                }
            }.let { methodMap ->
                return RouteMapper(null, methodMap)
            }
        }

        fun from(vararg methods: RpcMethod): HandlerMapper = from(methods.toList())
    }

}