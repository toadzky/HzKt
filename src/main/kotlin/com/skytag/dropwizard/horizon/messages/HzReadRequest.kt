package com.skytag.dropwizard.horizon.messages

data class HzReadRequest(override val requestId: Int,
                         override val type: HzRequestType,
                         val options: HzReadOptions) : HzRequestMessage