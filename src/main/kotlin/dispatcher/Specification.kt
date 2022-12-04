package dispatcher

import dto.*

enum class Specification(override val version: String) : Version, ContextBuilder {

    V1_0("1.0") {
        override fun isNotification(jsonrpc: String, id: String?): Boolean {
            TODO("Not yet implemented")
        }

        override fun build(jsonHolder: JsonHolder?): ContextHolder {
            TODO("Not yet implemented")
        }
    },

    V2_0("2.0") {
        override fun isNotification(jsonrpc: String, id: String?) = jsonrpc == version && id == null

        override fun build(jsonHolder: JsonHolder?): ContextHolder {
            if (jsonHolder == null) {
                return RpcContext.of(false, Response.error(version, PresetError.PARSE_ERROR)).done()
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
                val id = item.findValue("id")
                var idValue: String? = null
                var isNotification = false

                if (id != null) {
                    idValue = if (id.isTextual()) {
                        id.textValue()
                    } else if (id.isIntegralNumber()) {
                        id.asText()
                    } else {
                        responseList.add(Response.error(version, PresetError.INVALID_REQUEST))
                        continue
                    }
                } else {
                    idValue = null
                    isNotification = true
                }

                val jsonrpc = item.findValue("jsonrpc")
                if (jsonrpc == null || !jsonrpc.isTextual() || jsonrpc.textValue() != version) {
                    responseList.add(Response.error(version, PresetError.INVALID_REQUEST))
                    continue
                }

                val method = item.findValue("method")

                if (method == null || method.isNull() || !method.isTextual()) {
                    responseList.add(Response.error(version, PresetError.INVALID_REQUEST))
                    continue
                }

                val params = item.findValue("params")

                if (params != null && params.isNull()) {
                    responseList.add(Response.error(version, PresetError.INVALID_REQUEST))
                    continue
                }


            }



            TODO("Not yet implemented")
        }
    };

    abstract override fun build(jsonHolder: JsonHolder?): ContextHolder
}