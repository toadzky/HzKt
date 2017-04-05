package com.skytag.dropwizard.horizon.utils

import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by toadzky on 4/2/17.
 */
class PortAllocator {

    private val reserved = mutableSetOf<Int>()

    fun nextPort(): Int {
        val port = ThreadLocalRandom.current().ints(1024, 65_537)
                .filter { available(it) }
                .findFirst()
                .orElseThrow { IllegalStateException("port could not be allocated") }
        reserved.add(port)
        return port
    }

    fun reserve(port: Int): Boolean = if (available(port)) {
        reserved.add(port)
        true
    } else {
        false
    }

    private fun available(port: Int): Boolean {
        if (reserved.contains(port)) {
            return false
        }

        var serverSocket: ServerSocket? = null
        var dataSocket: DatagramSocket? = null
        try {
            serverSocket = ServerSocket(port)
            serverSocket.reuseAddress = true
            dataSocket = DatagramSocket(port)
            dataSocket.reuseAddress = true
            return true
        } catch (e: IOException) {
            return false
        } finally {
            if (dataSocket != null) {
                dataSocket.close()
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close()
                } catch (e: IOException) {
                    // can never happen
                }

            }
        }
    }
}
