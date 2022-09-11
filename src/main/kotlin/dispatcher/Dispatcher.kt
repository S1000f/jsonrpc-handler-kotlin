package dispatcher

import dto.Request
import method.RpcMethod

interface Dispatcher<T, R> : ContextBuilder {

    fun match(request: Request): RpcMethod?

    fun dispatch(jsonPayload: T): R?
}