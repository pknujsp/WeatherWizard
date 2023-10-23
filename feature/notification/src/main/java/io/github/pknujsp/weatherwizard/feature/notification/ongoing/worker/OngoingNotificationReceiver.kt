package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager


class OngoingNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {
            println("OngoingNotificationReceiver: ${intent.action}")
            val request = OneTimeWorkRequest.Builder(OngoingNotificationWorker::class.java)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag(OngoingNotificationWorker.name)
                .build()
            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniqueWork(OngoingNotificationWorker.name, ExistingWorkPolicy.KEEP, request)
        }
    }
}