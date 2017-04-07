package com.skytag.dropwizard.horizon

import com.google.common.net.InetAddresses
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.gen.ast.DbCreate
import com.skytag.dropwizard.horizon.codecs.HzHandshakeDecoder
import com.skytag.dropwizard.horizon.codecs.HzMessageEncoder
import com.skytag.dropwizard.horizon.codecs.HzRequestDecoder
import com.skytag.dropwizard.horizon.handlers.HorizonWebsocketEndpoint
import com.skytag.dropwizard.horizon.models.RethinkDbConnectionInfo
import io.dropwizard.ConfiguredBundle
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import mu.KLogging
import org.eclipse.jetty.util.component.AbstractLifeCycle
import org.eclipse.jetty.util.component.LifeCycle
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer
import java.net.InetAddress
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit.SECONDS
import javax.servlet.ServletException
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpointConfig
import javax.websocket.server.ServerEndpointConfig.Configurator


/**
 * Created by toadzky on 3/28/17.
 */
class HorizonBundle : ConfiguredBundle<HorizonConfigProvider> {

    companion object: KLogging()

    private lateinit var connectionInfo: RethinkDbConnectionInfo

    override fun initialize(bootstrap: Bootstrap<*>) {
        bootstrap.addBundle(AssetsBundle("/horizon"))
    }

    override fun run(config: HorizonConfigProvider, env: Environment) {
        connectionInfo = config.horizon.rethinkdb ?: RethinkDbConnectionInfo()


        val serverReady = if (config.horizon.startRethinkDb) {
            val rethinkServer = RethinkServer(http = connectionInfo.port,
                    boundAddresses = listOf(connectionInfo.host).map {
                        if (InetAddresses.isInetAddress(it)) {
                            InetAddresses.forString(it)
                        } else {
                            InetAddress.getByName(it)
                        }
                    })
            rethinkServer
                    .startAsync()
                    .awaitRunning()
            logger.info("RethinkDB Server started. driver-port=[${rethinkServer.driver}]")
            rethinkServer.ready
        } else {
            CompletableFuture.completedFuture(connectionInfo.port)
        }

        val connection = serverReady.thenApply { p ->
            val bldr = r.connection()
                    .hostname(connectionInfo.host)
                    .port(p)
                    .db(config.horizon.projectName)
            connectionInfo.user?.let { bldr.user(connectionInfo.user, connectionInfo.password) }
            bldr
        }.thenApply { it.connect() }

        connection.thenAccept {
            val dbList = r.dbList().run<List<String>>(it)
            if (!dbList.contains(config.horizon.projectName)) {
                r.dbCreate(config.horizon.projectName).run<DbCreate>(it)
            }
        }.whenComplete { _, ex ->
            ex?.let { logger.error("failed to create project database", ex) }
        }

        // TODO: If oauth providers registered in config, add oauth endpoints to jersey
        env.lifecycle().addLifeCycleListener(object : AbstractLifeCycle.AbstractLifeCycleListener() {

            override fun lifeCycleStarting(event: LifeCycle) {
                try {

                    val container: ServerContainer = WebSocketServerContainerInitializer.configureContext(env.applicationContext)
                    container.addEndpoint(ServerEndpointConfig.Builder.create(HorizonWebsocketEndpoint::class.java, "/horizon")
                            .configurator(object : Configurator() {
                                override fun <T : Any?> getEndpointInstance(endpointClass: Class<T>?): T
                                        = HorizonWebsocketEndpoint(connection.get(1, SECONDS)) as T
//                                        = HorizonWebsocket(HzReQL(connection.get())) as T
                            })
                            .subprotocols(listOf("rethinkdb-horizon-v0"))
                            .decoders(listOf(HzRequestDecoder::class.java, HzHandshakeDecoder::class.java))
                            .encoders(listOf(HzMessageEncoder::class.java))
                            .build()
                    )

                } catch (ex: ServletException) {
                    throw RuntimeException(ex)
                }

            }

        })

    }
}