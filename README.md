# Jsonrpc-handler-kotlin

Jsonrpc-handler-kotlin is a simple tool to handle JSON-RPC requests and responses.
It is designed to be used on the server side mainly. It offers classes and functions to fill JSON-RPC protocol requirements.

## 1. Dependencies

- jackson-module-kotlin:2.14.0

## 2. Quick start

This library contains no implementation of network layers. So, in this example, we will use Ktor and Spring Boot.
Note that JSON-RPC does not specify a certain protocol for messaging. You can use any network layer you want.

### 2.1 Ktor

```kotlin
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(SampleEchoMethod()))
    routing {
        post("/") {
            val receiveText = call.receiveText()
            jsonrpcHandler.dispatch(receiveText)?.let {
                call.respondText(contentType = ContentType.Application.Json, text = it)
            }
        }
    }
}
```

