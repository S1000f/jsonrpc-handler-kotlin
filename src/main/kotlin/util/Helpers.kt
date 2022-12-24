package util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

private val mapper = jacksonObjectMapper()
    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)

object Helpers {

    /**
     * Returns a Jackson-bind object mapper.
     *
     * It is configured to ignore unknown properties and empty beans.
     */
    fun getMapper(): ObjectMapper = mapper

    /**
     * Returns a JSON string from the given object by Jackson mapper. If the object is null, it returns null.
     *
     * It is configured to ignore unknown properties and empty beans.
     */
    fun <T> serialize(data: T): String? = try {
        mapper.writeValueAsString(data)
    } catch (e: JsonProcessingException) {
        e.printStackTrace()
        null
    }

    /**
     * Returns an object from the given JSON string by Jackson mapper. If the string is null, it returns null.
     *
     * It is configured to ignore unknown properties and empty beans.
     */
    fun <T> deserialize(json: String, type: TypeReference<T>): T? = try {
        mapper.readValue(json, type)
    } catch (e: JsonProcessingException) {
        e.printStackTrace()
        null
    }
}
