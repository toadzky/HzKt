package com.skytag.dropwizard.horizon.codecs

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.skytag.dropwizard.horizon.messages.HzMessage
import javax.websocket.Encoder.Text
import javax.websocket.EndpointConfig

/**
 * Created by toadzky on 3/31/17.
 */
class HzMessageEncoder : Text<HzMessage> {
    private val json = ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .setSerializationInclusion(NON_NULL)
            .registerKotlinModule()

    override fun init(config: EndpointConfig?) {}

    override fun destroy() {}

    override fun encode(thing: HzMessage): String = json.writeValueAsString(thing)
}

