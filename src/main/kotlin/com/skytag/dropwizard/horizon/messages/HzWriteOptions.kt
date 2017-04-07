package com.skytag.dropwizard.horizon.messages

import java.time.Duration

data class HzWriteOptions(val collection: String, val data: Array<Any>, val timeout: Duration? = null)