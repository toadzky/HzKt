package com.skytag.dropwizard.horizon.messages

import com.fasterxml.jackson.annotation.JsonCreator

sealed class HzRequestType {

    companion object {
        @JsonCreator
        @JvmStatic
        fun valueOf(name: String): HzRequestType {
            return when (name) {
                "keepalive" -> KeepAlive
                "unsubscribe" -> Unsubscribe
                "subscribe" -> Subscribe
                "insert" -> Insert
                "query" -> Query
                "remove" -> Remove
                "replace" -> Replace
                "store" -> Store
                "update" -> Update
                "upsert" -> Upsert
                "end_subscription" -> EndSubscription
                else -> throw IllegalArgumentException("unrecognized name: $name")
            }
        }
    }

    object KeepAlive : HzRequestType()
    object Unsubscribe : HzRequestType()
    object Subscribe : HzRequestType()
    object Insert : HzRequestType()
    object Query : HzRequestType()
    object Remove : HzRequestType()
    object Replace : HzRequestType()
    object Store : HzRequestType()
    object Update : HzRequestType()
    object Upsert : HzRequestType()
    object EndSubscription : HzRequestType()
}