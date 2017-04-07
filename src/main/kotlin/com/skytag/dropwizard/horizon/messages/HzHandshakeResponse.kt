package com.skytag.dropwizard.horizon.messages

data class HzHandshakeResponse(override val requestId: Int, val provider: String, val token: String): HzMessage