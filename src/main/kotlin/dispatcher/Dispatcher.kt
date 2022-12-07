package dispatcher

import dto.Request
import method.RpcMethod

interface Dispatcher<T, R> : ContextBuilder {

    val parser: JsonParser

    val matcher: (Request) -> RpcMethod?

    fun dispatch(jsonPayload: T): R?
}