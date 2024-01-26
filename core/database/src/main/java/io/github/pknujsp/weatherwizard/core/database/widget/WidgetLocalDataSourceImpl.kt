package io.github.pknujsp.weatherwizard.core.database.widget

import io.github.pknujsp.weatherwizard.core.database.zip.CompressionTool
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import java.time.ZonedDateTime

class WidgetLocalDataSourceImpl(
    private val widgetDao: WidgetDao, private val compressionTool: CompressionTool
) : WidgetLocalDataSource {

    override suspend fun add(widgetDto: WidgetDto): Int = widgetDao.insert(widgetDto).toInt()

    override suspend fun getAll(excludesResponseData: Boolean): List<WidgetDto> = widgetDao.run {
        if (excludesResponseData) {
            getAllWithoutResponseData()
        } else {
            getAll()
        }
    }.map {
        if (excludesResponseData) {
            it
        } else {
            it.deCompressed()
        }
    }

    override suspend fun getById(id: Int): WidgetDto? = widgetDao.getById(id)?.deCompressed()


    override suspend fun deleteById(id: Int) = widgetDao.deleteById(id)


    override suspend fun containsId(id: Int): Boolean = widgetDao.containsId(id)

    override suspend fun deleteAll() = widgetDao.deleteAll()


    override suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray?) {
        val compressed = responseData?.let { compressionTool.compress(it) }
        val finalStatus = if (compressed == null && status == WidgetStatus.RESPONSE_SUCCESS) {
            WidgetStatus.RESPONSE_FAILURE
        } else {
            status
        }
        widgetDao.updateResponseData(id, finalStatus, compressed, ZonedDateTime.now().toString())
    }

    private fun WidgetDto.deCompressed(): WidgetDto {
        return responseData?.let { copy(responseData = compressionTool.deCompress(it)) } ?: this
    }
}