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
        this.methodMap = ConcurrentHashMap(methodMap)
        this.matcher = matcher ?: match@{ req ->
            if (this.methodMap.isNotEmpty()) {
                val methodName = req.getMethodName()
                return@match this.methodMap[methodName]
            }

            return@match null
        }
    }

    override fun setMethod(methods: Collection<RpcMethod>) {
        this.methodMap.clear()

        methods.forEach { method ->
            this.methodMap[method.getName()] = method
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