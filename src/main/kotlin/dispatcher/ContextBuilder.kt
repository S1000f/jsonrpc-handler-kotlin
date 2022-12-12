package dispatcher

import dto.ContextHolder

/**
 * It has one interface property which is used to build a context from a json payload. Implementation of the property
 * must validate an argument if the data is valid for JSON-RPC 2.0 specification or not. It may accept various versions
 * of JSON-RPC specification or only one version.
 *
 * In case the property has noticed that the data is fit for a certain
 * version of JSON-RPC specification, it must return a context holder which contains a proper Response class
 * and is marked done, even though the data is not valid for the specification.
 * @since 1.0.0
 * @see JsonHolder
 * @see ContextHolder
 */
interface ContextBuilder {

    /**
     * Returns new [ContextHolder] from a JSON-RPC request which is a parsed json data.
     */
    val builder: (JsonHolder?) -> ContextHolder?

}