package com.skytag.dropwizard.horizon

/**
 * Created by toadzky on 3/28/17.
 */
data class HorizonConfiguration(
    val projectName: String? = "horizon",
    val startRethinkDb: Boolean? = null,
    val rethinkdb: RethinkDbConnectionInfo? = null,
    val allowAnonymous: Boolean? = null,
    val allowUnauthenticated: Boolean? = null
)

interface HorizonConfigProvider {
    val horizon: HorizonConfiguration
}