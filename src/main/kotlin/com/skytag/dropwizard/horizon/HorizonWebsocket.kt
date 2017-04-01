package com.skytag.dropwizard.horizon

import mu.KLogging
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint

/**
 * Created by toadzky on 3/30/17.
 */
@ServerEndpoint("/horizon",
        subprotocols = arrayOf("rethinkdb-horizon-v0"),
        decoders = arrayOf(HzRequestDecoder::class),
        encoders = arrayOf(HzMessageEncoder::class))
class HorizonWebsocket {
    companion object : KLogging()

    private lateinit var write: (Any?) -> Unit

    @OnOpen
    fun onConnect(session: Session) {
        logger.info("client connected. session=${session.id}")
        write = session.basicRemote::sendObject
    }

    @OnMessage
    fun onMessage(msg: HzMessage) {
        logger.info("received message. msg=[$msg]")

        when (msg) {
            is HzHandshake -> write(HzAuthenticationResponse(msg.requestId, msg.method, "token"))
            is HzKeepAlive -> write(HzAck(msg.requestId))
            is HzSubscribe -> {

            }
            else -> {
                logger.warn("unrecognized message. msg=[$msg]")
                write(HzAck(msg.requestId))
            }
        }
    }

}