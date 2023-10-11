package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetCurrentWeatherUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetHourlyForecastUseCase
import io.github.pknujsp.weatherwizard.feature.notification.common.INotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class OngoingNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val appSettingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {

    companion object : INotificationWorker {
        override val name: String = "OngoingNotificationWorker"
        override val id: UUID = UUID.fromString(name)
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {

            Result.success()
        }
    }
}