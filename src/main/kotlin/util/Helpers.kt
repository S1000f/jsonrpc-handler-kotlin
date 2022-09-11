package util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

private val mapper = jacksonObjectMapper()
    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)

private val mapperForRead = jacksonObjectMapper()
    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)

fun getMapper(): ObjectMapper = mapperForRead

fun <T> serialize(data: T): String? = try {
    mapper.writeValueAsString(data)
} catch (e: JsonProcessingException) {
    e.printStackTrace()
    null
}

fun <T> deserialize(json: String, type: TypeReference<T>): T? = try {
    mapperForRead.readValue(json, type)
} catch (e: JsonProcessingException) {
    e.printStackTrace()
    null
}

