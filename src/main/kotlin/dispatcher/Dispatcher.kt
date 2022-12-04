package dispatcher

import dto.Request
import method.RpcMethod

interface Dispatcher<T, R> : ContextBuilder {

    val parser: JsonParser

    fun match(request: Request): RpcMethod?

    fun dispatch(jsonPayload: T): R?
}