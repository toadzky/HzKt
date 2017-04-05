package com.skytag.dropwizard.horizon

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import javax.websocket.Encoder
import javax.websocket.EndpointConfig

/**
 * Created by toadzky on 3/31/17.
 */
class HzMessageEncoder : Encoder.Text<HzMessage> {
    private val json = ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .registerKotlinModule()

    override fun init(config: EndpointConfig?) {}

    override fun destroy() {}

    override fun encode(thing: HzMessage): String = json.writeValueAsString(thing)
}

