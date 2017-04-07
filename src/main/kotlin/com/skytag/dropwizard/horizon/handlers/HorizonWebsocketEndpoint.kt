package com.skytag.dropwizard.horizon.handlers

import com.rethinkdb.net.Connection
import com.skytag.dropwizard.horizon.messages.HzRequestType
import com.skytag.dropwizard.horizon.messages.HzRequestType.Insert
import com.skytag.dropwizard.horizon.operations.HorizonOperation
import com.skytag.dropwizard.horizon.operations.InsertOperation
import mu.KLogging
import javax.websocket.CloseReason
import javax.websocket.CloseReason.CloseCodes
import javax.websocket.Endpoint
import javax.websocket.EndpointConfig
import javax.websocket.Session

/**
 * Created by toadzky on 4/6/17.
 */
class HorizonWebsocketEndpoint(conn: Connection) : Endpoint() {
    companion object : KLogging()

    val operations = mapOf<HzRequestType, HorizonOperation>(
            Insert to InsertOperation(conn)
    )

    override fun onOpen(session: Session, config: EndpointConfig) {
        logger.info("session opened. id=[${session.id}] subprotocol=[${session.negotiatedSubprotocol}]")

        val handshakeHandler = HorizonHandshakeHandler(session, {
            session.addMessageHandler(HorizonMainHandler(session, operations))
        })
        session.addMessageHandler(handshakeHandler)
    }

    override fun onClose(session: Session, closeReason: CloseReason) {
        logger.info("session closed. id=[${session.id}] reason=[$closeReason]")
    }

    override fun onError(session: Session, thr: Throwable) {
        logger.error("error on webocket. id=[${session.id}]", thr)
        session.close(CloseReason(CloseCodes.CLOSED_ABNORMALLY, thr.message))
    }
}
