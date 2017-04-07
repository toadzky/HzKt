package com.skytag.dropwizard.horizon.codecs

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.skytag.dropwizard.horizon.messages.HzHandshake
import javax.websocket.Decoder
import javax.websocket.EndpointConfig

/**
 * Created by toadzky on 4/6/17.
 */
class HzHandshakeDecoder : Decoder.Text<HzHandshake> {
    override fun willDecode(s: String): Boolean = true

    override fun destroy() {
    }

    override fun init(config: EndpointConfig?) {
        // TODO: Pass in the object mapper
    }

    private val json = jacksonObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

    override fun decode(s: String): HzHandshake {

        return json.readValue(s, HzHandshake::class.java)

    }

}