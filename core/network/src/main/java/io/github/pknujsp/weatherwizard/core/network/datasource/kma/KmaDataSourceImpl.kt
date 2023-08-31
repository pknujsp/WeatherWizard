package io.github.pknujsp.weatherwizard.core.network.datasource.kma

import io.github.pknujsp.weatherwizard.core.network.api.kma.KmaNetworkApi
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser.KmaHtmlParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Response
import java.lang.ref.WeakReference
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

class KmaDataSourceImpl @Inject constructor(
    private val kmaNetworkApi: KmaNetworkApi,
    private val kmaHtmlParser: KmaHtmlParser,
) : KmaDataSource {
  override suspend fun getKmaCurrentConditions(code: String) = withContext(Dispatchers.Default) {
    kmaNetworkApi.getKmaCurrentConditions(code = code).onResponse().fold(
      onSuccess = {
        val parsedKmaCurrentCondition = kmaHtmlParser.parseCurrentConditions(
          document = WeakReference(
            Jsoup.parse(it),
          ).get()!!,
          baseDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toString(),
        )

        parsedKmaCurrentCondition?.run { Result.success(this) } ?: run {
          Result.failure(Throwable("Failed to parse KMA current conditions"))
        }
      },
      onFailure = { Result.failure(it) },
    )
  }

  override suspend fun getKmaHourlyAndDailyForecast(code: String) = withContext(Dispatchers.Default) {
    kmaNetworkApi.getKmaHourlyAndDailyForecast(code = code).onResponse().fold(
      onSuccess = {
        val parsedKmaHourlyForecasts = kmaHtmlParser.parseHourlyForecasts(
          document = WeakReference(
            Jsoup.parse(it),
          ).get(),
        )

        val parsedKmaDailyForecasts = kmaHtmlParser.parseDailyForecasts(
          document = WeakReference(
            Jsoup.parse(it),
          ).get(),
        )

        Result.success(Pair(parsedKmaHourlyForecasts, parsedKmaDailyForecasts))
      },
      onFailure = { Result.failure(it) },
    )
  }

  private fun Response<String>.onResponse(): Result<String> {
    return if (isSuccessful and !body().isNullOrEmpty()) Result.success(body()!!)
    else Result.failure(Throwable(errorBody()?.toString() ?: "Unknown error"))
  }
}