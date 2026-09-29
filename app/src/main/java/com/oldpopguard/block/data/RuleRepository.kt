package com.oldpopguard.block.data

import android.content.Context
import android.util.Log
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

    suspend fun initIfNeeded() {
        val count = dao.getRuleCount()
        if (count == 0) {
            Log.d(TAG, "首次启动，导入内置规则")
            dao.insertRules(BuiltinRules.getInitialRules())
        }
        refreshIndex()
    }

    suspend fun syncRules(): Boolean = withContext(Dispatchers.IO) {
        // 暂时不需要网络同步，直接返回true
        Log.d(TAG, "暂时跳过网络同步")
        return@withContext true
    }

    suspend fun refreshIndex() {
        val allRules = dao.getAllRules()
        RuleIndex.rebuild(allRules)
    }

    suspend fun getRuleCount(): Int = dao.getRuleCount()
}
