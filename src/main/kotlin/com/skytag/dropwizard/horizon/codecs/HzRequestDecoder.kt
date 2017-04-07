package com.skytag.dropwizard.horizon.codecs

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.skytag.dropwizard.horizon.messages.HzKeepAlive
import com.skytag.dropwizard.horizon.messages.HzReadRequest
import com.skytag.dropwizard.horizon.messages.HzRequestMessage
import com.skytag.dropwizard.horizon.messages.HzRequestType
import com.skytag.dropwizard.horizon.messages.HzRequestType.EndSubscription
import com.skytag.dropwizard.horizon.messages.HzRequestType.Insert
import com.skytag.dropwizard.horizon.messages.HzRequestType.KeepAlive
import com.skytag.dropwizard.horizon.messages.HzRequestType.Query
import com.skytag.dropwizard.horizon.messages.HzRequestType.Remove
import com.skytag.dropwizard.horizon.messages.HzRequestType.Replace
import com.skytag.dropwizard.horizon.messages.HzRequestType.Store
import com.skytag.dropwizard.horizon.messages.HzRequestType.Subscribe
import com.skytag.dropwizard.horizon.messages.HzRequestType.Unsubscribe
import com.skytag.dropwizard.horizon.messages.HzRequestType.Update
import com.skytag.dropwizard.horizon.messages.HzRequestType.Upsert
import com.skytag.dropwizard.horizon.messages.HzWriteRequest
import mu.KLogging
import java.io.IOException
import javax.websocket.Decoder
import javax.websocket.EndpointConfig

/**
 * Created by toadzky on 3/31/17.
 */
class HzRequestDecoder : Decoder.Text<HzRequestMessage> {
    companion object : KLogging() {

        private val json = jacksonObjectMapper()
                .setPropertyNamingStrategy(SNAKE_CASE)

    }

    override fun init(config: EndpointConfig?) {
    }

    override fun willDecode(s: String?): Boolean = true

    override fun decode(s: String): HzRequestMessage? {
        val tree = json.readTree(s)

        return when {
            tree.isObject -> when (HzRequestType.valueOf(tree["type"].textValue())) {
                in arrayOf(Insert, Update, Upsert, Store) -> json.treeToValue(tree, HzWriteRequest::class.java)
                in arrayOf(Query, Subscribe) -> json.treeToValue(tree, HzReadRequest::class.java)
                KeepAlive -> HzKeepAlive(tree["request_id"].intValue())
                EndSubscription -> object : HzRequestMessage {
                    override val requestId = tree["request_id"].intValue()
                    override val type = EndSubscription
                }
                in arrayOf(Unsubscribe, Replace, Remove) -> {
                    logger.debug("currently unsupported request receieved. type=[${tree["type"].textValue()}]")
                    null
                }
                else -> throw IOException("unrecognized message type")
            }
            else -> throw IOException("message body was not a json object")
        }
    }

    override fun destroy() {}

//        private val parser = object : JsonDeserializer<HzRequestMessage>() {
//            override fun deserialize(p: JsonParser, ctx: DeserializationContext): HzRequestMessage? {
//                p.nextToken() // should be start object
//                p.nextToken() // should be request_id
//                val id = p.valueAsInt
//                while (p.nextToken() != END_OBJECT) {
//                    val firstKey = p.currentName
//                    p.nextToken()
//                    return when (firstKey) {
//                        "method" -> HzHandshake(id, p.valueAsString)
//
//                        "type" -> {
//                            return when (p.valueAsString) {
//                                "keepalive" -> HzKeepAlive(id)
//                                "subscribe" -> {
//                                    p.nextToken()
//                                    return makeSubscribe(p, id)
//                                }
//                                in arrayOf("store", "insert") -> {
//                                    val type = HzRequestType.fromString(p.valueAsString)
//                                    p.nextValue()
//                                    return HzWriteRequest(id, type, json.treeToValue<HzWriteOptions>(p.readValueAsTree()))
//                                }
//                                else -> throw IOException("unrecognized message type. type=[${p.valueAsString}]")
//                            }
//                        }
//                        else -> throw IOException("unrecognized message type")
//                    }
//                }
//                return null
//            }
//
//            private fun makeSubscribe(p: JsonParser, id: Int): HzRequestMessage {
//                p.nextValue()
//                return HzReadRequest(id, Subscribe, p.codec.readValue(p, HzReadOptions::class.java))
//            }
//
//        }
}

