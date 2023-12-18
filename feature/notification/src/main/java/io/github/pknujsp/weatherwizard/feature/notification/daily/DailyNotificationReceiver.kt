package io.github.pknujsp.weatherwizard.feature.notification.daily

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager


class DailyNotificationReceiver : BroadcastReceiver() {

    companion object {
        fun bundleOf(notificationId: Long): Bundle = bundleOf("notificationId" to notificationId)
    }

    @SuppressLint("RestrictedApi")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {
            val data = Data(mapOf("notificationId" to intent.extras?.getLong("notificationId")))
            val request = OneTimeWorkRequest.Builder(DailyNotificationWorker::class.java)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag(DailyNotificationWorker.name)
                .setInputData(data)
                .build()
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniqueWork(DailyNotificationWorker.name, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
        }
    }
}