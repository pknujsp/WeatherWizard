package io.github.pknujsp.weatherwizard.feature.notification.daily

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationWorker


class DailyNotificationReceiver : BroadcastReceiver() {

    companion object {
        fun bundleOf(notificationId: Long): Bundle = bundleOf("notificationId" to notificationId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {
            val request = OneTimeWorkRequest.Builder(OngoingNotificationWorker::class.java)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag(OngoingNotificationWorker.name)
                .build()
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniqueWork(OngoingNotificationWorker.name, ExistingWorkPolicy.KEEP, request)
        }
    }
}