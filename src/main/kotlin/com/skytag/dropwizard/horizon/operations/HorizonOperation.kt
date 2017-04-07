package com.skytag.dropwizard.horizon.operations

import com.skytag.dropwizard.horizon.messages.HzRequestMessage
import com.skytag.dropwizard.horizon.messages.HzResponse
import io.reactivex.Observable

/**
 * Created by toadzky on 4/6/17.
 */
interface HorizonOperation {

    fun process(request: HzRequestMessage): Observable<HzResponse>

}