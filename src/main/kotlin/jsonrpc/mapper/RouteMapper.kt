package jsonrpc.mapper

import jsonrpc.dto.Request
import jsonrpc.method.AbstractRouteMethod
import jsonrpc.method.HttpRouteMethod
import jsonrpc.method.RpcMethod

/**
 * A default implementation of [HandlerMapper]. It inherits [AbstractHandlerMapper].
 *
 * It matches a route method with the given request.
 * @see AbstractRouteMethod
 */
class RouteMapper private constructor(
    matcher: ((Request) -> RpcMethod?)?,
    methodMap: Map<String, RpcMethod>
) : AbstractHandlerMapper(matcher, methodMap) {

    companion object {

        /**
         * Returns a new instance of [RouteMapper] that has a default matcher and no methods.
         */
        fun empty(): HandlerMapper = RouteMapper(null, emptyMap())

        /**
         * Returns a new instance of [RouteMapper] that has the matcher input and an empty method map.
         */
        fun <T : AbstractRouteMethod> from(matcher: (Request) -> T?): HandlerMapper = RouteMapper(matcher, emptyMap())

        /**
         * Returns a new instance of [RouteMapper] that maps [HttpRouteMethod] to endpoints. [endpointMap] is a map that
         * has a method name as a key and an endpoint string as a value.
         *
         * The endpoint string specifies the destination for a [RpcMethod] that can deal with the given request.
         *
         * e.g. `"http://192.168.0.3:8081/jsonrpc"`
         * @see HttpRouteMethod
         */
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