package com.skytag.dropwizard.horizon

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.END_OBJECT
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import mu.KLogging
import java.io.IOException
import java.io.Reader
import javax.websocket.Decoder
import javax.websocket.EndpointConfig

/**
 * Created by toadzky on 3/31/17.
 */
class HzRequestDecoder : Decoder.TextStream<HzMessage> {

    private val json = ObjectMapper()
            .registerKotlinModule()
            .registerModule(SimpleModule()
                    .addDeserializer(HzMessage::class.java, parser)
            )

    override fun init(config: EndpointConfig?) {}

    override fun destroy() {}

    override fun decode(reader: Reader): HzMessage {
        return json.readValue(reader, HzMessage::class.java)
    }

    companion object: KLogging() {
        private val parser = object : JsonDeserializer<HzMessage>() {
            override fun deserialize(p: JsonParser, ctx: DeserializationContext): HzMessage? {
                p.nextToken() // should be "request_id"
                val id = p.valueAsInt
                while (p.nextToken() != END_OBJECT) {
                    p.nextToken()
                    val firstKey = p.currentName
                    p.nextToken()
                    return when (firstKey) {
                        "method" -> HzHandshake(id, p.valueAsString)

                        "type" -> {
                            when (p.valueAsString) {
                                "keepalive" -> HzKeepAlive(id)
                                "subscribe" -> {
                                    p.nextToken()
                                    return makeSubscribe(p, id)
                                }
                                else -> throw IOException("unrecognized message type. type=[${p.valueAsString}]")
                            }
                        }
                        else -> throw IOException("unrecognized message type")
                    }
                }
                return null
            }

            private fun makeSubscribe(p: JsonParser, id: Int): HzSubscribe {
                var options = HzSubscriptionOptions()

                while (p.nextToken() != END_OBJECT) {
                    when (p.currentName) {
                        "options" ->  {
                            options = p.codec.readValue(p, HzSubscriptionOptions::class.java)
                        }
                        else -> throw IOException("unrecognized field: ${p.currentName}")
                    }
                }

                return HzSubscribe(id, options)
            }
        }
    }
}

