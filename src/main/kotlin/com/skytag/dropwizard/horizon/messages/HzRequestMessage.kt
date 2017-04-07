package com.skytag.dropwizard.horizon.messages

interface HzRequestMessage : HzMessage {
    val type: HzRequestType
}