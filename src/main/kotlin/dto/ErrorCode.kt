package dto

/**
 * It has two properties: the [code] and [message]. It is used to represent an error code and a corresponding message
 * in the failure response.
 *
 * The range of [code] values from -32768 to -32000 is reserved for pre-defined errors in the JSON-RPC 2.0 specification.
 * It is recommended to use a single phrase as the [message] value.
 */
interface ErrorCode {

    /**
     * It represents an error code. There is no restriction on the value of the error code being a negative integer
     * in the specification.
     */
    val code: Int

    /**
     * It represents a message that is used to briefly describe the error. It is recommended to use a single phrase
     * as the message value.
     */
    val message: String
}