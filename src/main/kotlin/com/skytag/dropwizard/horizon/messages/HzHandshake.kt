package com.skytag.dropwizard.horizon.messages

data class HzHandshake(override val requestId: Int, val method: String) : HzMessage