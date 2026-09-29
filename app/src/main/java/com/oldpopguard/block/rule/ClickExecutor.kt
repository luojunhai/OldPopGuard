package com.oldpopguard.block.rule

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo
import java.util.concurrent.ConcurrentHashMap

object ClickExecutor {
    private val lastClickTime = ConcurrentHashMap<String, Long>()

    val GLOBAL_WHITELIST = setOf(
        "com.icbc", "com.ccb.android", "com.chinamworld.main", "com.cmbchina.mobile",
        "com.android.bankabc", "com.spdb.mobilebank", "com.citicbank",
        "com.eg.android.AlipayGphone", "com.tencent.mm", "com.unionpay",
        "com.android.systemui", "com.android.settings",
        "com.android.packageinstaller", "com.oldpopguard.block"
    )

    fun executeClick(service: AccessibilityService, node: AccessibilityNodeInfo, packageName: String, rule: RuleEntity): Boolean {
        if (GLOBAL_WHITELIST.contains(packageName)) return false
        val now = System.currentTimeMillis()
        val last = lastClickTime[packageName] ?: 0L
        if (now - last < rule.minIntervalMs) return false
        return try {
            val result = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            if (result) lastClickTime[packageName] = now
            result
        } catch (e: Exception) { false }
    }

    fun isWhitelisted(packageName: String): Boolean {
        return GLOBAL_WHITELIST.any { packageName.startsWith(it) }
    }
}
