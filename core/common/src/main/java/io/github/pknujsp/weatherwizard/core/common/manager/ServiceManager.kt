package io.github.pknujsp.weatherwizard.core.common.manager

class ServiceManager {


    fun isServiceRunning(serviceType: ServiceType): Boolean {
        return false
    }

}



enum class ServiceType(val id:Int){
    LOCATION(1),
}