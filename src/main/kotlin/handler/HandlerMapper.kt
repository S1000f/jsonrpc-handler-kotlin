package handler

import dto.Request
import method.RpcMethod

interface HandlerMapper {

    var matcher: (Request) -> RpcMethod?

    fun setMethod(methods: Collection<RpcMethod>)

    fun addMethod(method: RpcMethod)

    fun addMethods(methods: Collection<RpcMethod>)

}