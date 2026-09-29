package com.oldpopguard.block.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import com.oldpopguard.block.rule.ClickExecutor
import com.oldpopguard.block.rule.NodeFinder
import com.oldpopguard.block.rule.RuleIndex

class PopupAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "PopupGuard"
        @Volatile var instance: PopupAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "无障碍服务已连接")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> handleWindowEvent(event)
        }
    }

    private fun handleWindowEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        if (ClickExecutor.isWhitelisted(packageName)) return
        val rules = RuleIndex.getRulesForPackage(packageName)
        if (rules.isEmpty()) return
        val rootNode = NodeFinder.getRootNode(this) ?: return
        for (rule in rules) {
            try {
                val closeNode = NodeFinder.findCloseButton(rootNode, rule)
                if (closeNode != null) {
                    val clicked = ClickExecutor.executeClick(this, closeNode, packageName, rule)
                    if (clicked) {
                        Log.d(TAG, "已自动关闭弹窗: pkg=$packageName, rule=${rule.id}")
                        break
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "规则匹配异常: ${rule.id}", e)
            }
        }
    }

    override fun onInterrupt() {}
    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
