package com.oldpopguard.block.rule

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RuleIndex {
    private val index = mutableMapOf<String, List<RuleEntity>>()
    private var globalRules: List<RuleEntity> = emptyList()
    private val _rulesVersion = MutableStateFlow(0)
    val rulesVersion: StateFlow<Int> = _rulesVersion.asStateFlow()

    fun rebuild(allRules: List<RuleEntity>) {
        index.clear()
        val enabledRules = allRules.filter { it.enabled }
        globalRules = enabledRules.filter { it.packageName == "*" }
        enabledRules.filter { it.packageName != "*" }.forEach { rule ->
            val list = index.getOrPut(rule.packageName) { mutableListOf() }
            index[rule.packageName] = list + rule
        }
        _rulesVersion.value += 1
    }

    fun getRulesForPackage(packageName: String): List<RuleEntity> {
        val specific = index[packageName] ?: emptyList()
        return specific + globalRules
    }

    fun clear() {
        index.clear()
        globalRules = emptyList()
        _rulesVersion.value += 1
    }
}
