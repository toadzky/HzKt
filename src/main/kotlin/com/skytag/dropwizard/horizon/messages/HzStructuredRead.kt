package com.skytag.dropwizard.horizon.messages

data class HzStructuredRead(override val collection: String,
                            val limit: Int? = null,
                            val order: HzOrderingClause? = null,
                            val above: HzMatchingClause? = null,
                            val below: HzMatchingClause? = null,
                            val findAll: List<Map<String,Any>>? = null) : HzReadOptions