package com.skytag.dropwizard.horizon

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.Writer
import javax.websocket.Encoder
import javax.websocket.EndpointConfig

/**
 * Created by toadzky on 3/31/17.
 */
class HzMessageEncoder : Encoder.TextStream<HzMessage> {
    private val json = ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .registerKotlinModule()

    override fun init(config: EndpointConfig?) {}

    override fun destroy() {}

    override fun encode(thing: HzMessage, writer: Writer) {
        json.writeValue(writer, thing)
    }
}

