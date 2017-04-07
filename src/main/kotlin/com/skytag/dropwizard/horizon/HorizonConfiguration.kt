package com.skytag.dropwizard.horizon

import com.skytag.dropwizard.horizon.models.RethinkDbConnectionInfo

/**
 * Created by toadzky on 3/28/17.
 */
data class HorizonConfiguration(
        val projectName: String = "horizon",
        val startRethinkDb: Boolean = false,
        val rethinkdb: RethinkDbConnectionInfo? = null,
        val allowAnonymous: Boolean? = null,
        val allowUnauthenticated: Boolean? = null
)

interface HorizonConfigProvider {
    val horizon: HorizonConfiguration
}