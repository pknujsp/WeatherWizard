package io.github.pknujsp.weatherwizard.core.common.manager

object ServiceManager {

    fun isServiceRunning(worker: IWorker): Boolean = worker.isRunning.get()

}


enum class ServiceType(val id: Int) {
    LOCATION(1),
}