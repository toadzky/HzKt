package com.skytag.dropwizard.horizon.messages

data class HzWriteRequest(override val requestId: Int,
                          override val type: HzRequestType,
                          val options: HzWriteOptions) : HzRequestMessage