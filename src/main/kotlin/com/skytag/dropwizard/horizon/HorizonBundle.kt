package com.skytag.dropwizard.horizon

import io.dropwizard.ConfiguredBundle
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.eclipse.jetty.util.component.AbstractLifeCycle
import org.eclipse.jetty.util.component.LifeCycle
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer
import javax.servlet.ServletException
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpointConfig
import javax.websocket.server.ServerEndpointConfig.Configurator


/**
 * Created by toadzky on 3/28/17.
 */
class HorizonBundle : ConfiguredBundle<HorizonConfigProvider> {

    private var connectionInfo: RethinkDbConnectionInfo? = null

    override fun initialize(bootstrap: Bootstrap<*>) {
        bootstrap.addBundle(AssetsBundle("/horizon"))
    }

    override fun run(config: HorizonConfigProvider, env: Environment) {
        connectionInfo = config.horizon.rethinkdb

        if (connectionInfo == null && config.horizon.startRethinkDb ?: true) {
            connectionInfo = RethinkDbConnectionInfo()
            // TODO: Spin up a rethinkdb instance
        }

        // TODO: Register websocket connectors with jersey
        // TODO: If oauth providers registered in config, add oauth endpoints to jersey
        // TODO: Add horizon endpoints to jersey
        env.lifecycle().addLifeCycleListener(object : AbstractLifeCycle.AbstractLifeCycleListener() {

            override fun lifeCycleStarting(event: LifeCycle) {
                try {

                    val container: ServerContainer = WebSocketServerContainerInitializer.configureContext(env.applicationContext)
//                    container.addEndpoint(HorizonWebsocket::class.java)
                    container.addEndpoint(ServerEndpointConfig.Builder.create(HorizonWebsocket::class.java, "/horizon")
                            .configurator(object : Configurator() {
                                override fun <T : Any?> getEndpointInstance(endpointClass: Class<T>?): T = HorizonWebsocket() as T
                            })
                            .build()
                    )

                } catch (ex: ServletException) {
                    throw RuntimeException(ex)
                }

            }

        })

    }
}