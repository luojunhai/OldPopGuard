package com.oldpopguard.block.rule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popup_rules")
data class RuleEntity(
    @PrimaryKey val id: String,
    val packageName: String,
    val closeTexts: List<String>,
    val closeViewIds: List<String>,
    val closeContentDescs: List<String>,
    val popupKeywords: List<String>,
    val minIntervalMs: Long = 2000,
    val maxDepth: Int = 15,
    val enabled: Boolean = true,
    val version: Int = 1,
    val updatedAt: Long = System.currentTimeMillis()
)
