package io.github.pknujsp.everyweather.core.widgetnotification.model

import io.github.pknujsp.everyweather.core.model.mock.MockDataGenerator
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.DefaultRemoteViewCreator
import java.time.ZonedDateTime

class RemoteViewsMockGenerator : MockDataGenerator() {
    companion object {
        val header by lazy {
            DefaultRemoteViewCreator.Header(address = "서울특별시 강남구", lastUpdated = ZonedDateTime.now())
        }
    }
}