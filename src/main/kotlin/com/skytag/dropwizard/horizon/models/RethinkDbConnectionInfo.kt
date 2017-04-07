package com.skytag.dropwizard.horizon.models

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