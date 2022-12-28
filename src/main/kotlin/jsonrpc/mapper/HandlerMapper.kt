package jsonrpc.mapper

import jsonrpc.dto.Request
import jsonrpc.method.RpcMethod

/**
 * It is used to find a method that is matched with the given request.
 *
 * The implementation of this interface can either have a container of methods and find a method from the container,
 * or just use a matcher to find a method without a container.
 * @see RpcMethod
 */
interface HandlerMapper {

    /**
     * Returns a method that is matched with the given request. If there is no method matched, this returns null.
     */
    var matcher: (Request) -> RpcMethod?

    /**
     * Replace all methods with the given methods.
     */
    fun setMethod(methods: Collection<RpcMethod>)

    /**
     * Add a method to the container.
     */
    fun addMethod(method: RpcMethod)

    /**
     * Add methods to the container.
     */
    fun addMethods(methods: Collection<RpcMethod>)

}