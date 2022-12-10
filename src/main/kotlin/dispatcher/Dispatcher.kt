package dispatcher

import dto.ContextHolder
import dto.Request
import method.RpcMethod

interface Dispatcher<T, R> {

    val parser: JsonParser

    val contextBuilder: ContextBuilder

    fun build(jsonHolder: JsonHolder): ContextHolder? = contextBuilder.builder(jsonHolder)

    fun match(request: Request): RpcMethod?

    fun dispatch(jsonPayload: T?): R?
}