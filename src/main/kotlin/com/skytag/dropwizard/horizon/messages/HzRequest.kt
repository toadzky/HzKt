package com.skytag.dropwizard.horizon.messages

interface HzRequest: HzMessage {
    val type: String
}