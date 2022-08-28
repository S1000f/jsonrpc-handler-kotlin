package dispatcher

import dto.ContextHolder
import dto.Request
import method.RpcMethod

interface Dispatcher<T, R> {
    fun buildContext(jsonHolder: JsonHolder): ContextHolder?

    fun match(request: Request): RpcMethod?

    fun dispatch(jsonPayload: T): R?
}