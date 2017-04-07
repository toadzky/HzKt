package com.skytag.dropwizard.horizon.messages

import com.skytag.dropwizard.horizon.messages.RequestState.complete

data class HzAck(override val requestId: Int,
                 override val state: RequestState = complete,
                 val data: Any? = null) : HzResponse