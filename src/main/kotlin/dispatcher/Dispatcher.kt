package dispatcher

import dto.ContextHolder
import dto.Request
import method.RpcMethod

/**
 * It is the entry point of the library. It takes a JSON-RPC request and returns a JSON-RPC response.
 * Implementation of this interface must validate [T] and convert it to [ContextHolder] and then,
 * return [R] at last.
 *
 * If the returned value is not null, it must be a valid JSON-RPC response which is ready to ship.
 * @param T The type of the JSON-RPC request.
 * @param R The type of the JSON-RPC response.
 */
interface Dispatcher<T, R> {

    val parser: JsonParser

    val contextBuilder: ContextBuilder

    /**
     * Returns a [ContextHolder] which is built from [JsonHolder]. It uses [ContextBuilder] to build a context
     * if there is no overriding of this method.
     */
    fun build(jsonHolder: JsonHolder): ContextHolder? = contextBuilder.builder(jsonHolder)

    /**
     * Returns a method which is matched with the given request. If there is no method matched, it must return null.
     * It can use [mapper.HandlerMapper] to find a method.
     */
    fun match(request: Request): RpcMethod?

    /**
     * Returns a JSON-RPC response from a JSON-RPC request. If the request contains only Notifications it will
     * return null.
     */
    fun dispatch(jsonPayload: T?): R?
}