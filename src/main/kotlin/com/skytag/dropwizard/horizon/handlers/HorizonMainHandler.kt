package com.skytag.dropwizard.horizon.handlers

import com.skytag.dropwizard.horizon.messages.HzAck
import com.skytag.dropwizard.horizon.messages.HzRequestMessage
import com.skytag.dropwizard.horizon.messages.HzRequestType
import com.skytag.dropwizard.horizon.messages.HzRequestType.EndSubscription
import com.skytag.dropwizard.horizon.messages.HzRequestType.KeepAlive
import com.skytag.dropwizard.horizon.messages.HzResponse
import com.skytag.dropwizard.horizon.operations.HorizonOperation
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import javax.websocket.MessageHandler
import javax.websocket.Session

/**
 * Created by toadzky on 4/6/17.
 */
class HorizonMainHandler(session: Session, private val operations: Map<HzRequestType, HorizonOperation>) : MessageHandler.Whole<HzRequestMessage> {
    companion object : KLogging()

    val write: (Any) -> Unit
    private val subscriptions = mutableMapOf<Int, Observable<HzResponse>>()

    init {
      write = session.basicRemote::sendObject
    }

    override fun onMessage(message: HzRequestMessage) {
        when (message.type) {
            KeepAlive -> {
                write(HzAck(message.requestId))
                return
            }
            EndSubscription -> {
                subscriptions[message.requestId]?.unsubscribeOn(Schedulers.computation())
                return
            }
        }

        val op = operations[message.type]
        if (op == null) {
            logger.warn("unhandled request. type=[${message.type}]")
            write(HzAck(message.requestId))
            return
        }

        op.process(message).subscribe(
                { write(it) },
                { logger.error("operation failed. request=[$message]", it) },
                { subscriptions.remove(message.requestId) }
        )
    }
}