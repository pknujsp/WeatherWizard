package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class OngoingNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {
            val request = OneTimeWorkRequest.Builder(OngoingNotificationWorker::class.java).addTag(OngoingNotificationWorker.name).build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}