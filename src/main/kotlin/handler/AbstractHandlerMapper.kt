package handler

import dto.Request
import method.RpcMethod
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

abstract class AbstractHandlerMapper protected constructor(
    matcher: ((Request) -> RpcMethod?)?,
    methodMap: Map<String, RpcMethod> = emptyMap()
) : HandlerMapper {

    final override var matcher: (Request) -> RpcMethod?
    protected val methodMap: ConcurrentMap<String, RpcMethod>

    init {
        this.matcher = matcher ?: match@{ req ->
            if (methodMap.isNotEmpty()) {
                val methodName = req.getMethodName()
                return@match methodMap[methodName]
            }

            return@match null
        }
        this.methodMap = ConcurrentHashMap(methodMap)
    }

    override fun setMethod(methods: Collection<RpcMethod>) {
        methodMap.clear()

        methods.forEach { method ->
            methodMap[method.getName()] = method
        }
    }

    override fun addMethod(method: RpcMethod) {
        methodMap[method.getName()] = method
    }

    override fun addMethods(methods: Collection<RpcMethod>) {
        methods.forEach { method ->
            methodMap.putIfAbsent(method.getName(), method)
        }
    }

}