package io.github.pknujsp.weatherwizard.core.model.nominatim

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class GeoCodeEntity(
    val placeId: Long,
    val displayName: String,
    val countryCode: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val county: String,
    val city: String,
    val province: String,
    val road: String,
    val quarter: String,
    val natural: String,
    val state: String,
    val suburb: String,
    val category: String,
    val osmType: String,
) : EntityModel {

    /*
    Country (국가): 가장 큰 지리적 단위로, 독립된 주권을 가진 영토를 의미합니다.
    Province/State (주/도): 국가 내에서 더 작은 행정 구역으로, 종종 자체적인 정부를 가지고 있습니다.
    City (시): 주 또는 도 안에 있는 도시 단위로, 독립적인 지방 자치단체를 형성할 수 있습니다.
    County (군): 미국 같은 국가에서는 시보다 더 작은 단위로, 여러 개의 타운이나 빌리지를 포함할 수 있습니다.
    Suburb (교외): 대도시나 도시의 외곽 지역을 의미합니다.
    Saddle, Natural (고개): 이는 일반적인 행정 구역보다는 지리적 특성을 나타내는 용어로, 두 높은 지점 사이의 낮은 지점을 말합니다.
    Quarter (구/동): 도시 내의 더 작은 단위로, 주거 지역이나 상업 지역 등을 구분하는 데 사용됩니다.
    Road (도로): 가장 작은 지리적 단위로, 특정 거리나 도로를 나타냅니다.
    */
    val simpleDisplayName: String = StringBuilder().apply {
        if (province.isNotEmpty()) append(" ").append(province)
        if (city.isNotEmpty()) append(" ").append(city)
        if (county.isNotEmpty()) append(" ").append(county)
        if (suburb.isNotEmpty()) append(" ").append(suburb)
        if (natural.isNotEmpty()) append(" ").append(natural)
        if (quarter.isNotEmpty()) append(" ").append(quarter)

        if (isEmpty()) append(displayName.replace(", $country", ""))
    }.toString().trim()
}