package dto

import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RequestTest {

    private val JSON_WITH_PARAMS_BY_POSITION = """
        {"jsonrpc":"2.0","method":"subtract","params":[42,23],"id":1}
    """.trimIndent()

    private val JSON_WITH_NO_PARAMS = """
        {"jsonrpc":"2.0","method":"no-params","id":1}
    """.trimIndent()

    private val JSON_NOTIFICATION = """
        {"jsonrpc":"2.0","method":"notification","params":"hi"}
    """.trimIndent()

    private val JSON_BATCH = """
        [{"jsonrpc":"2.0","method":"subtract","params":[42,23],"id":1},{"jsonrpc":"2.0","method":"subtract","params":[12,10],"id":2}]
    """.trimIndent()


}