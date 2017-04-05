package com.skytag.dropwizard.horizon.decoders

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.END_OBJECT
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.skytag.dropwizard.horizon.HzHandshake
import com.skytag.dropwizard.horizon.HzKeepAlive
import com.skytag.dropwizard.horizon.HzMessage
import com.skytag.dropwizard.horizon.HzReadOptions
import com.skytag.dropwizard.horizon.HzReadRequest
import com.skytag.dropwizard.horizon.HzRequestMessage
import com.skytag.dropwizard.horizon.HzRequestType.Store
import com.skytag.dropwizard.horizon.HzRequestType.Subscribe
import com.skytag.dropwizard.horizon.HzWriteOptions
import com.skytag.dropwizard.horizon.HzWriteRequest
import mu.KLogging
import java.io.IOException
import java.io.Reader
import javax.websocket.Decoder.TextStream
import javax.websocket.EndpointConfig

/**
 * Created by toadzky on 3/31/17.
 */
class HzRequestDecoder : TextStream<HzMessage> {

    override fun init(config: EndpointConfig?) {
        json.registerModule(SimpleModule().addDeserializer(HzMessage::class.java, parser))
    }

    override fun destroy() {}

    override fun decode(reader: Reader): HzMessage {
        return json.readValue(reader, HzMessage::class.java)
    }

    companion object : KLogging() {
        private val json = jacksonObjectMapper()
                .registerModule(SimpleModule().addDeserializer(HzReadOptions::class.java, HzReadOptionsDecoder()))

        private val parser = object : JsonDeserializer<HzMessage>() {
            override fun deserialize(p: JsonParser, ctx: DeserializationContext): HzMessage? {
                p.nextToken() // should be start object
                p.nextToken() // should be request_id
                val id = p.valueAsInt
                while (p.nextToken() != END_OBJECT) {
                    val firstKey = p.currentName
                    p.nextToken()
                    return when (firstKey) {
                        "method" -> HzHandshake(id, p.valueAsString)

                        "type" -> {
                            return when (p.valueAsString) {
                                "keepalive" -> HzKeepAlive(id)
                                "subscribe" -> {
                                    p.nextToken()
                                    return makeSubscribe(p, id)
                                }
                                "store" -> {
                                    p.nextValue()
                                    return HzWriteRequest(id, Store, json.treeToValue<HzWriteOptions>(p.readValueAsTree()))
                                }
                                else -> throw IOException("unrecognized message type. type=[${p.valueAsString}]")
                            }
                        }
                        else -> throw IOException("unrecognized message type")
                    }
                }
                return null
            }

            private fun makeSubscribe(p: JsonParser, id: Int): HzRequestMessage {
                p.nextValue()
                return HzReadRequest(id, Subscribe, p.codec.readValue(p, HzReadOptions::class.java))
            }

        }
    }
}

