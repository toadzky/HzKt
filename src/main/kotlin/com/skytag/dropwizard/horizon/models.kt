package com.skytag.dropwizard.horizon

import com.skytag.dropwizard.horizon.HzOrderDirection.Ascending
import com.skytag.dropwizard.horizon.HzRequestType.KeepAlive
import com.skytag.dropwizard.horizon.RequestState.complete
import java.time.Duration

/**
 * Created by toadzky on 3/28/17.
 */
data class RethinkDbConnectionInfo(val host: String = "localhost", val port: Int = 28015, val user: String? = null, val password: String? = null) {
    fun connectionString(database: String? = null): String {
        val bldr = StringBuilder().append("rethinkdb://")
        user?.let {
            bldr.append(it)
            password?.let { bldr.append(":$it") }
            bldr.append("@")
        }
        bldr.append("$host:$port")
        database?.let { bldr.append("/$it") }
        return bldr.toString()
    }
}

data class HzOAuthProvider(val id: String, val secret: String)

interface HzMessage {
    val requestId: Int
}
interface HzRequest: HzMessage {
    val type: String
}

enum class RequestState {
    complete
}

interface HzResponse: HzMessage {
    val state: RequestState
}

data class HzHandshake(override val requestId: Int, val method: String) : HzMessage

enum class HzRequestType {
    KeepAlive, Unsubscribe, Subscribe, Insert, Query, Remove, Replace, Store, Update, Upsert
}

interface HzRequestMessage : HzMessage{
    val type: HzRequestType
}

data class HzKeepAlive(override val requestId: Int) : HzRequestMessage {
    override val type: HzRequestType = KeepAlive
}
data class HzReadRequest(override val requestId: Int, override val type: HzRequestType, val options: HzReadOptions) : HzRequestMessage

data class HzAck(override val requestId: Int, override val state: RequestState = complete) : HzResponse

data class HzAuthenticationResponse(override val requestId: Int, val provider: String, val token: String): HzMessage

data class HzWriteAck(override val requestId: Int, override val state: RequestState = complete, val data: Any) : HzResponse

interface HzReadOptions {
    val collection: String
}
data class HzFindOptions(override val collection: String, val find: Map<String,Any>) : HzReadOptions
data class HzStructuredRead(override val collection: String,
                       val limit: Int? = null,
                       val order: HzOrderingClause? = null,
                       val above: HzMatchingClause? = null,
                       val below: HzMatchingClause? = null,
                       val findAll: List<Map<String,Any>>? = null) : HzReadOptions

enum class HzOrderDirection { Ascending, Descending }
data class HzOrderingClause(val fields: Array<String>, val direction: HzOrderDirection = Ascending)

enum class HzBoundsType { Open, Closed }
data class HzMatchingClause(val value: Map<String,Any>, val bound: HzBoundsType)

data class HzWriteRequest(override val requestId: Int,
                          override val type: HzRequestType,
                          val options: HzWriteOptions) : HzRequestMessage
data class HzWriteOptions(val collection: String, val data: Array<Any>, val timeout: Duration? = null)

interface DataRecord {
    val id: Any
}