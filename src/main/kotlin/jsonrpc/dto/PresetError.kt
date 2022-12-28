package jsonrpc.dto

/**
 * This enum class contains the error codes of the JSON-RPC 2.0 specification.
 * - [PARSE_ERROR]
 * - [INVALID_REQUEST]
 * - [METHOD_NOT_FOUND]
 * - [INVALID_PARAMS]
 * - [INTERNAL_ERROR]
 */
enum class PresetError(override val code: Int, override val message: String) : ErrorCode {

    /**
     * It is used when the server receives an invalid JSON format.
     */
    PARSE_ERROR(-32700, "Parse error"),

    /**
     * It is used when the server receives a valid JSON format, but the request is not a valid JSON-RPC 2.0 request.
     */
    INVALID_REQUEST(-32600, "Invalid Request"),

    /**
     * It is used when the server receives a valid JSON-RPC 2.0 request, but the method is not found.
     *
     * There is no restriction in the specification on the value of the method name being an empty string.
     */
    METHOD_NOT_FOUND(-32601, "Method not found"),

    /**
     * It is used when the server receives a valid JSON-RPC 2.0 request, but the parameter is not valid.
     *
     * In the specification, the `prams` field in a request is optional. So checking the `params` is a responsibility
     * of the matched method.
     */
    INVALID_PARAMS(-32602, "Invalid params"),

    /**
     * It is used when the server receives a valid JSON-RPC 2.0 request, but there is an internal error in the server.
     */
    INTERNAL_ERROR(-32603, "Internal error");

}