package jsonrpc.method

import com.fasterxml.jackson.core.type.TypeReference
import jsonrpc.dto.Request
import jsonrpc.dto.Response

/**
 * This interface validates parameters and deals with the request. It is not necessary or optional to use this interface
 * to handle requests and produce responses.
 */
interface RpcMethod {

    /**
     * Returns a method name. The name means the method name in the request, not this class name.
     */
    fun getName(): String

    /**
     * Returns a type of the parameters. If the method does not need any parameters, it should return null.
     */
    fun getParamsType(): TypeReference<*>?

    /**
     * This method returns a response object after handling the request. If the request is invalid, it returns a failure response.
     * The [params] parameter is an object that is parsed from the JSON string in the request
     * and has the type specified by [getParamsType]. If [getParamsType] returns null, [params] will be null as well.
     *
     * If the request is a Notification, this method may return null.
     */
    fun handle(request: Request, params: Any?): Response?
}