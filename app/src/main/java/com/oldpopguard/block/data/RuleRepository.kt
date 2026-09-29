package com.oldpopguard.block.data

import android.content.Context
import android.util.Log
import com.oldpopguard.block.network.RuleApi
import com.oldpopguard.block.rule.BuiltinRules
import com.oldpopguard.block.rule.RuleIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RuleRepository(private val context: Context) {
    companion object {
        private const val TAG = "RuleRepository"
        @Volatile private var INSTANCE: RuleRepository? = null
        fun getInstance(context: Context): RuleRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RuleRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val dao = AppDatabase.getInstance(context).ruleDao()
    private val api = RuleApi.create()

    suspend fun initIfNeeded() {
        val count = dao.getRuleCount()
        if (count == 0) {
            Log.d(TAG, "首次启动，导入内置规则")
            dao.insertRules(BuiltinRules.getInitialRules())
        }
        refreshIndex()
    }

    suspend fun syncRules(): Boolean = withContext(Dispatchers.IO) {
        try {
            val localVersion = dao.getMaxVersion() ?: 0
            val response = api.getRules(since = localVersion)
            val remoteRules = response.rules
            if (remoteRules.isEmpty()) return@withContext false
            val entities = com.oldpopguard.block.rule.RuleMapper.toEntityList(remoteRules)
            dao.insertRules(entities)
            refreshIndex()
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "规则同步失败", e)
            return@withContext false
        }
    }

    suspend fun refreshIndex() {
        val allRules = dao.getAllRules()
        RuleIndex.rebuild(allRules)
    }

    suspend fun getRuleCount(): Int = dao.getRuleCount()
}
