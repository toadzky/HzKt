package com.skytag.dropwizard.horizon.messages

import com.skytag.dropwizard.horizon.messages.HzOrderDirection.Ascending

data class HzOrderingClause(val fields: Array<String>, val direction: HzOrderDirection = Ascending)