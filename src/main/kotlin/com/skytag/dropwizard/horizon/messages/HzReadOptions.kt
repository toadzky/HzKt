package com.skytag.dropwizard.horizon.messages

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.skytag.dropwizard.horizon.codecs.HzReadOptionsDecoder

@JsonDeserialize(using = HzReadOptionsDecoder::class)
interface HzReadOptions {
    val collection: String
}