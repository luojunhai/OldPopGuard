package com.oldpopguard.block.worker

import android.content.Context
import androidx.work.*
import com.oldpopguard.block.data.RuleRepository
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

class RuleSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        private const val WORK_NAME = "rule_sync_worker"
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val workRequest = PeriodicWorkRequestBuilder<RuleSyncWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest
            )
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
        return@coroutineScope try {
            val repo = RuleRepository.getInstance(applicationContext)
            repo.syncRules()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
