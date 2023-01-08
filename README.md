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

## 3. Create a method
You can create a method by implementing the `RpcMethod` interface.

```kotlin
class AddMethod : RpcMethod {
    override fun getName(): String = "add"

    override fun getParamsType(): TypeReference<*> = object : TypeReference<List<Int>>() {}

    override fun handle(request: Request, params: Any?): Response {
        val list = params as? List<Int> ?: return Response.error(PresetError.INVALID_PARAMS)
        return when (list.size) {
            0, 1 -> Response.error(PresetError.INVALID_PARAMS)
            else -> Response.success(list.sum(), request)
        }
    }
}
```

In this example, the method name is 'add', and this method requires `List<Int>` as a parameter type.
You can cast `params` without checking its type, as the dispatcher will check the parameter type 
and return an error response if it does not match the required `List<Int>` type.

```kotlin
fun Application.module() {
    val jsonrpcHandler = JsonrpcHandler(MethodMapper.from(SampleEchoMethod(), AddMethod()))
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
Attach the method to the method mapper.

```http request
POST http://localhost:8080
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "id": 0,
  "method": "add",
  "params": [42, 42]
}
```
```
HTTP/1.1 200 OK
Content-Length: 38
Content-Type: application/json; charset=UTF-8
Connection: keep-alive

{
  "result": 84,
  "jsonrpc": "2.0",
  "id": "0"
}
```
Now that we have created a method and registered it with the dispatcher, we can call it remotely.


```kotlin
data class MultiplyEachParams(val list: List<Int>, val multiplier: Int)
class MultiplyEachMethod : RpcMethod {
    override fun getName() = "multiplyEach"

    override fun getParamsType() = object : TypeReference<MultiplyEachParams>() {}

    override fun handle(request: Request, params: Any?): Response {
        val parameter = params as? MultiplyEachParams
            ?: return Response.error(PresetError.INVALID_PARAMS)
        val list = parameter.list
        val multiplier = parameter.multiplier
        return Response.success(MultiplyEachParams(list.map { it * multiplier }, multiplier), request)
    }
}
```

It is possible to use not only Kotlin Collections but specific classes as a parameter types.
Note that you don't have to mark the data class with `@Serializable` annotation.

```http request
POST http://localhost:8080
Content-Type: application/json

{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "multiplyEach",
  "params": {
    "list": [1, 2, 3],
    "multiplier": 2
  }
}
```
```
HTTP/1.1 200 OK
Content-Length: 67
Content-Type: application/json; charset=UTF-8
Connection: keep-alive

{
  "result": {
    "list": [2, 4, 6],
    "multiplier": 2
  },
  "jsonrpc": "2.0",
  "id": "1"
}
```

In the JSON-RPC request, the `params` field of the JSON object should match the structure of the `MultiplyEachParams` class.
The dispatcher will automatically deserialize the JSON object into an instance of the `MultiplyEachParams` class 
and pass it to the handle method.