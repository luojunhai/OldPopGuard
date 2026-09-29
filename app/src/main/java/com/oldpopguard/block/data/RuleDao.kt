package com.oldpopguard.block.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.oldpopguard.block.rule.RuleEntity

@Dao
interface RuleDao {
    @Query("SELECT * FROM popup_rules")
    suspend fun getAllRules(): List<RuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<RuleEntity>)

    @Query("SELECT COUNT(*) FROM popup_rules")
    suspend fun getRuleCount(): Int

    @Query("SELECT MAX(version) FROM popup_rules")
    suspend fun getMaxVersion(): Int?
}
