package dispatcher

import dto.*

/**
 * It validates a request for JSON-RPC specification and builds a [ContextHolder] at the same time.
 */
enum class Specification(override val version: String) : Version, ContextBuilder {

    /**
     * JSON-RPC 1.0 specification.
     */
    V1_0("1.0") {

        override fun isNotification(jsonrpc: String, id: String?): Boolean {
            TODO("Not yet implemented")
        }

        // TODO: implement
        override val builder: (JsonHolder?) -> ContextHolder? = { _ -> null }
    },

    /**
     * JSON-RPC 2.0 specification.
     */
    V2_0("2.0") {

        override fun isNotification(jsonrpc: String, id: String?) = jsonrpc == version && id == null

        override val builder: (JsonHolder?) -> ContextHolder? = builder@{ jsonHolder ->
            if (jsonHolder == null) {
                return@builder RpcContext.of(false, Response.error(PresetError.PARSE_ERROR)).done()
            }

            var isBatch = false
            val list = mutableListOf<JsonHolder>()

            if (jsonHolder.isArray() && !jsonHolder.isEmpty()) {
                for (item in jsonHolder) {
                    list.add(item)
                }

                isBatch = true

            } else {
                list.add(jsonHolder)
            }

            val requestList = mutableListOf<Request>()
            val responseList = mutableListOf<Response>()

            for (item in list) {
                val jsonrpc = item.findValue("jsonrpc")

                if (jsonrpc == null || !jsonrpc.isTextual() || jsonrpc.textValue() != version) {
                    responseList.add(Response.error(PresetError.INVALID_REQUEST))
                    continue
                }

                val id = item.findValue("id")
                var idValue: String?
                var isNotification = false

                if (id != null) {
                    val integralNumber = id.isIntegralNumber()
                    val text = id.isTextual()
                    val aNull = id.isNull()

                    if (!integralNumber && !text && !aNull) {
                        responseList.add(Response.error(PresetError.INVALID_REQUEST))
                        continue
                    }

                    idValue = id.asText()

                } else {
                    idValue = null
                    isNotification = true
                }

                val method = item.findValue("method")?.textValue() ?: ""

                if (method.isEmpty()) {
                    responseList.add(Response.error(PresetError.INVALID_REQUEST))
                    continue
                }

                val params = item.findValue("params")

                if (params != null && params.isNull()) {
                    responseList.add(Response.error(PresetError.INVALID_REQUEST))
                    continue
                }

                val requestImpl = RequestImpl(method, isNotification, item.toString(), params?.toString(), id = idValue)
                requestList.add(requestImpl)
            }

            return@builder RpcContext.of(isBatch, requestList, responseList)
                .let { if (it.getRequests().isEmpty()) return@builder it.done() else it }
        }
    };

    companion object {
        /**
         * Returns a [ContextBuilder] that validates a request with all versions of JSON-RPC in the [Specification] enum class.
         */
        fun contextBuilder() = object : ContextBuilder {
            override val builder: (JsonHolder?) -> ContextHolder = { jsonHolder ->
                Specification.values()
                    .firstNotNullOfOrNull { it.builder(jsonHolder) }
                    ?: RpcContext.of(false, Response.error(PresetError.INVALID_REQUEST)).done()
            }
        }
    }
}
