package com.skytag.dropwizard.horizon.decoders

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.skytag.dropwizard.horizon.HzFindOptions
import com.skytag.dropwizard.horizon.HzReadOptions
import com.skytag.dropwizard.horizon.HzStructuredRead

/**
 * Created by toadzky on 4/4/17.
 */
class HzReadOptionsDecoder : JsonDeserializer<HzReadOptions>() {
    override fun deserialize(p: JsonParser, ctx: DeserializationContext): HzReadOptions {
        val tree = p.readValueAsTree<ObjectNode>()
        return (p.codec as ObjectMapper).treeToValue(tree, if (tree.get("find") == null) {
            HzStructuredRead::class.java
        } else {
            HzFindOptions::class.java
        })
    }
}