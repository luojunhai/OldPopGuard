package com.oldpopguard.block.network

import com.oldpopguard.block.rule.RuleListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RuleApi {
    @GET("api/rules")
    suspend fun getRules(@Query("since") sinceVersion: Int): RuleListResponse

    companion object {
        fun create(): RuleApi {
            throw NotImplementedError("暂时不需要网络请求")
        }
    }
}
