package handler

import dto.Request
import method.RpcMethod

interface HandlerMapper {

    fun match(request: Request): RpcMethod?

    fun setMatcher(matcher: (Request) -> RpcMethod?)

    fun setMethod(methods: Collection<RpcMethod>)

    fun addMethod(method: RpcMethod)

    fun addMethods(methods: Collection<RpcMethod>)
}