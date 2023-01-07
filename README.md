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

This server listens to POST requests on the root path. `call.receiveText()` receives the request body as a string.
`jsonrpcHandler.dispatch(receiveText)` dispatches the request to the method mapper and returns the response as a string
if the request is not a Notification. `call.respondText(contentType = ContentType.Application.Json, text = it)` responds
with a JSON string.

Note that in this example, the `ktor-server-content-negotiation` plugin is not required.

```http request
POST http://localhost:8080
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "id": 0,
  "method": "echo"
}
```
Start the server, and we can now invoke the 'echo' method on the server remotely

```http
HTTP/1.1 200 OK
Content-Length: 76
Content-Type: application/json; charset=UTF-8
Connection: keep-alive

{
  "result": {
    "method": "echo",
    "timestamp": 1673097162
  },
  "jsonrpc": "2.0",
  "id": "0"
}
```
the body of the response is the expected JSON-RPC response.

### 2.2 Spring Boot

```kotlin
@SpringBootApplication
class DemoJsonrpcSpringApplication {
    @Bean
    fun jsonrpcHandler() = JsonrpcHandler(MethodMapper.from(SampleEchoMethod()))
}

fun main(args: Array<String>) {
    runApplication<DemoJsonrpcSpringApplication>(*args)
}

@RestController
class Controller(val jsonRpcHandler: Dispatcher<String, String>) {
    @PostMapping(produces = ["application/json"])
    fun handle(@RequestBody message: String): String? = 
        jsonRpcHandler.dispatch(message)?.let { return it }
}
```
Create Bean of `JsonrpcHandler` and inject it to the controller.
In this example, the handler method produces 'application/json' by `@PostMapping(produces = ["application/json"])`.
