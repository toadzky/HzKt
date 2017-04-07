package com.skytag.dropwizard.horizon.handlers

import com.skytag.dropwizard.horizon.messages.HzHandshake
import com.skytag.dropwizard.horizon.messages.HzHandshakeResponse
import mu.KLogging
import java.util.UUID.randomUUID
import javax.websocket.CloseReason
import javax.websocket.CloseReason.CloseCodes
import javax.websocket.MessageHandler
import javax.websocket.Session

/**
 * Created by toadzky on 4/6/17.
 */
class HorizonHandshakeHandler(private val session: Session, private val callback: () -> Unit) : MessageHandler.Whole<HzHandshake> {
    companion object: KLogging()

    override fun onMessage(handshake: HzHandshake) {
        logger.debug("received handshake. request_id=[${handshake.requestId}] method=[${handshake.method}]")

        if (handshake.method == "unauthenticated") {
            session.asyncRemote.sendObject(HzHandshakeResponse(handshake.requestId, handshake.method, randomUUID().toString()))
            session.removeMessageHandler(this)
            callback()
        } else {
            session.close(CloseReason(CloseCodes.CANNOT_ACCEPT, "Only authentication method currently supported is 'unauthenticated'."))
        }
    }
}