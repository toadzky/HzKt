package com.skytag.dropwizard.horizon.operations

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.rethinkdb.RethinkDB
import com.rethinkdb.model.OptArgs
import com.rethinkdb.net.Connection
import com.skytag.dropwizard.horizon.messages.HzAck
import com.skytag.dropwizard.horizon.messages.HzRequestMessage
import com.skytag.dropwizard.horizon.messages.HzResponse
import com.skytag.dropwizard.horizon.messages.HzWriteRequest
import io.reactivex.Observable
import mu.KLogging

/**
 * Created by toadzky on 4/6/17.
 */
class InsertOperation(private val db: Connection) : HorizonOperation {

    companion object : KLogging() {
        private val json = jacksonObjectMapper()
    }

    override fun process(request: HzRequestMessage): Observable<HzResponse> {

        return Observable.fromCallable {

                    (request as? HzWriteRequest)?.let {
                        val data = it.options.data.map {
                            val tree = json.valueToTree<ObjectNode>(it)
                            // TODO: Put this as a constant somewhere
                            tree.put("\$hz_v\$", 0)
                        }.map { json.treeToValue<Map<String,Any>>(it) }
                        RethinkDB.r.table(it.options.collection).insert(data).run<Map<String,Any>>(db, OptArgs.of("return_changes", "always"))
                    } ?: throw IllegalStateException("failed to insert")

                }
                .map { (it["generated_keys"] as List<*>).map { mapOf("id" to it, "\$hz_v\$" to 0) } }
                .map { HzAck(request.requestId, data = it) }

    }
}