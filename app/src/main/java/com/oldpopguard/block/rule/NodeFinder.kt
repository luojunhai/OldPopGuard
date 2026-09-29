package com.oldpopguard.block.rule

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo

object NodeFinder {
    fun findCloseButton(rootNode: AccessibilityNodeInfo?, rule: RuleEntity): AccessibilityNodeInfo? {
        if (rootNode == null) return null
        if (!isPopupWindow(rootNode, rule)) return null
        return traverseFind(rootNode, rule, 0)
    }

    private fun isPopupWindow(rootNode: AccessibilityNodeInfo, rule: RuleEntity): Boolean {
        if (rule.popupKeywords.isEmpty()) return true
        val allText = StringBuilder()
        collectAllText(rootNode, allText, 0, rule.maxDepth)
        val text = allText.toString()
        return rule.popupKeywords.any { text.contains(it, ignoreCase = true) }
    }

    private fun traverseFind(node: AccessibilityNodeInfo, rule: RuleEntity, depth: Int): AccessibilityNodeInfo? {
        if (depth > rule.maxDepth) return null
        if (isCloseButton(node, rule)) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = traverseFind(child, rule, depth + 1)
            if (result != null) return result
        }
        return null
    }

    private fun isCloseButton(node: AccessibilityNodeInfo, rule: RuleEntity): Boolean {
        if (!node.isClickable) return false
        val nodeText = node.text?.toString()?.trim()
        if (nodeText != null) {
            for (closeText in rule.closeTexts) {
                if (nodeText.equals(closeText, ignoreCase = true)) return true
                if (closeText.length <= 2 && nodeText.length <= 3 && nodeText.contains(closeText, ignoreCase = true)) return true
            }
        }
        val viewId = node.viewIdResourceName
        if (viewId != null) {
            for (closeId in rule.closeViewIds) {
                if (viewId.endsWith(closeId) || viewId.contains(closeId)) return true
            }
        }
        val desc = node.contentDescription?.toString()?.trim()
        if (desc != null) {
            for (closeDesc in rule.closeContentDescs) {
                if (desc.equals(closeDesc, ignoreCase = true)) return true
            }
        }
        return false
    }

    private fun collectAllText(node: AccessibilityNodeInfo, builder: StringBuilder, depth: Int, maxDepth: Int) {
        if (depth > maxDepth) return
        node.text?.let { builder.append(it).append(" ") }
        node.contentDescription?.let { builder.append(it).append(" ") }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectAllText(child, builder, depth + 1, maxDepth)
        }
    }

    fun getRootNode(service: AccessibilityService): AccessibilityNodeInfo? {
        return try { service.rootInActiveWindow } catch (e: Exception) { null }
    }
}
