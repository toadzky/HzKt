package com.skytag.dropwizard.horizon.messages

import com.skytag.dropwizard.horizon.messages.HzRequestType.KeepAlive

data class HzKeepAlive(override val requestId: Int) : HzRequestMessage {
    override val type: HzRequestType = KeepAlive
}