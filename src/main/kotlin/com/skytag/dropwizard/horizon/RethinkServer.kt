package com.skytag.dropwizard.horizon

import com.google.common.net.InetAddresses
import com.google.common.util.concurrent.AbstractIdleService
import com.skytag.dropwizard.horizon.utils.PortAllocator
import java.net.InetAddress
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit.SECONDS

/**
 * Start up a RethinkDB server for Horizon to use. Defaults to localhost, random port
 * Created by toadzky on 4/2/17.
 */

private val ports = PortAllocator()
private val localhost = InetAddresses.forString("127.0.0.1")

/**
 *
 * @param requestedDriverPort the port to have RethinkDB listen for driver connections. This is a polite request only. If the port isn't available, another will be selected.
 */
class RethinkServer(
        http: Int = 0,
        requestedDriverPort: Int = 0,
        directory: Path = Paths.get("rethinkdb_data"),
        boundAddresses: List<InetAddress> = listOf(localhost),
        cacheSize: Int = 200)
: AbstractIdleService() {

    private lateinit var proc: Process
    // We need the actual port it's going to use, so instead of trying to parse the lines of the
    // processes output to find it, just randomly select one and
    val driver: Int = requestedDriverPort.takeUnless { it == 0 }?.takeIf(ports::reserve) ?: ports.nextPort()
    private val cmd: Array<String>

    init {
        cmd = arrayOf("rethinkdb",
                "--http-port", "$http",
                "--cluster-port", "0",
                "--driver-port", "$driver",
                "--cache-size", "$cacheSize",
                "--directory", "$directory",
                "--no-update-check") + boundAddresses.flatMap { listOf("--bind", it.hostAddress ?: it.hostName) }
    }

    override fun startUp() {
        proc = ProcessBuilder()
                .command(*cmd)
                .inheritIO()
                .start()
    }

    override fun shutDown() {
        if (proc.isAlive) {
            proc.destroy()
            proc.waitFor(20, SECONDS)
            if (proc.isAlive) {
                proc.destroyForcibly()
            }
        }
    }
}