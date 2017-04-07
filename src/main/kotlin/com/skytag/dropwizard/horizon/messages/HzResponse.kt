package com.skytag.dropwizard.horizon.messages

interface HzResponse: HzMessage {
    val state: RequestState
}