package com.skytag.dropwizard.horizon

import com.fasterxml.jackson.annotation.JsonIgnore
import com.skytag.dropwizard.horizon.RequestState.complete

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

data class HzHandshake(override val requestId: Int, val method: String) : HzMessage {
    val type = "handshake"
}
data class HzKeepAlive(override val requestId: Int) : HzMessage {
    val type = "keepalive"
}
data class HzSubscribe(override val requestId: Int, val options: HzSubscriptionOptions) : HzMessage {
    val type = "subscribe"
}

data class HzAck(override val requestId: Int, override val state: RequestState = complete) : HzResponse

data class HzAuthenticationResponse(override val requestId: Int, val provider: String, val token: String): HzMessage {
    @JsonIgnore val type = "token"
}

data class HzSubscriptionOptions(val collection: String? = null)