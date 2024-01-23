package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FakeWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return Result.success()
    }
}