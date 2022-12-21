package mapper

import dto.Request
import method.RpcMethod

/**
 * A default implementation of [HandlerMapper]. It inherits [AbstractHandlerMapper].
 * It provides some useful static factory methods to create a new instance.
 */
class MethodMapper private constructor(
    matcher: ((Request) -> RpcMethod?)?,
    methodMap: Map<String, RpcMethod>
) : AbstractHandlerMapper(matcher, methodMap) {

    companion object {

        /**
         * Returns a new instance of [MethodMapper] that has a default matcher and no methods.
         */
        fun empty(): HandlerMapper = MethodMapper(null, emptyMap())

        /**
         * Returns a new instance of [MethodMapper] that has the matcher input and an empty method map.
         */
        fun from(matcher: (Request) -> RpcMethod?): HandlerMapper = MethodMapper(matcher, emptyMap())

        /**
         * Returns a new instance of [MethodMapper] that has a default matcher and the given methods.
         */
        fun from(methods: Collection<RpcMethod>): HandlerMapper =
            mutableMapOf<String, RpcMethod>()
                .apply {
                    methods.forEach { method ->
                        putIfAbsent(method.getName(), method)
                    }
                }.let { methodMap -> MethodMapper(null, methodMap) }

        /**
         * Returns a new instance of [MethodMapper] that has the matcher input and the given methods.
         */
        fun from(vararg methods: RpcMethod): HandlerMapper = from(methods.toList())
    }

}