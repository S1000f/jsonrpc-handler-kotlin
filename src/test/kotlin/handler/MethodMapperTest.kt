package handler

import method.HttpRouteMethod
import method.RpcMethod
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MethodMapperTest {

    @Test
    fun instanceTest() {
        val empty = MethodMapper.empty()

        empty.matcher = { method ->
            HttpRouteMethod(method.getMethodName(), "http://localhost:8080/${method.getMethodName()}")
        }

        println(empty.matcher)

        empty.matcher = { null}

        println(empty.matcher)
    }
}