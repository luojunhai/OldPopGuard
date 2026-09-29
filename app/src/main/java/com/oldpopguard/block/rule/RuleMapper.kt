package com.oldpopguard.block.rule

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RuleDto(
    val id: String,
    val packageName: String,
    val closeTexts: List<String> = emptyList(),
    val closeViewIds: List<String> = emptyList(),
    val closeContentDescs: List<String> = emptyList(),
    val popupKeywords: List<String> = emptyList(),
    val minIntervalMs: Long = 2000,
    val maxDepth: Int = 15,
    val enabled: Boolean = true,
    val version: Int = 1
)

@Serializable
data class RuleListResponse(val version: Int, val rules: List<RuleDto>)

object RuleMapper {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    fun fromJson(jsonStr: String): RuleListResponse = json.decodeFromString(RuleListResponse.serializer(), jsonStr)
    fun toEntity(dto: RuleDto): RuleEntity = RuleEntity(
        id = dto.id, packageName = dto.packageName, closeTexts = dto.closeTexts,
        closeViewIds = dto.closeViewIds, closeContentDescs = dto.closeContentDescs,
        popupKeywords = dto.popupKeywords, minIntervalMs = dto.minIntervalMs,
        maxDepth = dto.maxDepth, enabled = dto.enabled, version = dto.version
    )
    fun toEntityList(dtos: List<RuleDto>): List<RuleEntity> = dtos.map { toEntity(it) }
}

object BuiltinRules {
    fun getInitialRules(): List<RuleEntity> = listOf(
        RuleEntity("miui_system_ad_1", "com.miui.system", listOf("关闭", "跳过", "×"), listOf("iv_close", "btn_close"), emptyList(), listOf("广告", "推荐"), minIntervalMs = 3000),
        RuleEntity("miui_security_ad_1", "com.miui.securitycenter", listOf("关闭", "跳过"), listOf("iv_close"), emptyList(), listOf("广告", "推荐", "清理"), minIntervalMs = 3000),
        RuleEntity("huawei_system_ad_1", "com.huawei.systemmanager", listOf("关闭", "跳过", "我知道了"), listOf("iv_close"), emptyList(), listOf("广告", "推荐"), minIntervalMs = 3000),
        RuleEntity("oppo_system_ad_1", "com.oppo.system", listOf("关闭", "跳过"), listOf("iv_close"), emptyList(), listOf("广告", "推荐"), minIntervalMs = 3000),
        RuleEntity("quickapp_ad_1", "com.miui.quickappcenter", listOf("关闭", "跳过"), listOf("iv_close"), emptyList(), listOf("广告", "热门"), minIntervalMs = 2000),
        RuleEntity("uc_browser_ad_1", "com.uc.browser", listOf("取消", "关闭", "暂不"), listOf("btn_cancel"), emptyList(), listOf("下载", "安装", "推荐"), minIntervalMs = 3000),
        RuleEntity("global_skip_ad", "*", listOf("跳过广告", "跳过"), listOf("tv_skip", "btn_skip"), emptyList(), listOf("广告"), minIntervalMs = 5000, maxDepth = 20),
        RuleEntity("global_cancel_dialog", "*", listOf("取消", "暂不", "以后再说"), listOf("btn_cancel"), emptyList(), listOf("更新", "升级", "开启通知", "评分"), minIntervalMs = 5000, maxDepth = 10)
    )
}
