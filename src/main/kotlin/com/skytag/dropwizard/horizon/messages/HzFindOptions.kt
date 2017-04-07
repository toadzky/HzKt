package com.skytag.dropwizard.horizon.messages

data class HzFindOptions(override val collection: String, val find: Map<String,Any>) : HzReadOptions