package com.oldpopguard.block.network

import com.oldpopguard.block.rule.RuleListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RuleApi {
    @GET("api/rules")
    suspend fun getRules(@Query("since") sinceVersion: Int): RuleListResponse

    companion object {
        private const val BASE_URL = "https://your-server.com/"
        fun create(): RuleApi {
            val okHttpClient = okhttp3.OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(retrofit2.kotlinx.serialization.KotlinSerializationConverterFactory.create(json))
                .build()
            return retrofit.create(RuleApi::class.java)
        }
    }
}
