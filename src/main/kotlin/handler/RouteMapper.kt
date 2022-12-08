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

        fun from(matcher: (Request) -> RpcMethod?): HandlerMapper = RouteMapper(matcher, emptyMap())

        fun fromEndpointMap(endpointMap: Map<String, String>): HandlerMapper =
            from(endpointMap.map { (name, endpoint) -> HttpRouteMethod(name, endpoint) })

        private fun from(methods: Collection<RpcMethod>): HandlerMapper =
            mutableMapOf<String, RpcMethod>()
                .apply {
                    methods.forEach { method ->
                        putIfAbsent(method.getName(), method)
                    }
                }.let { methodMap -> RouteMapper(null, methodMap) }
    }

}