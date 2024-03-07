package io.github.pknujsp.everyweather.core.network.api.kma.parser


import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.ProbabilityValueType
import org.jsoup.nodes.Document
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class KmaHtmlParser {

    private val zoneId = ZoneId.of("Asia/Seoul")
    private val degree = "℃"
    private val mm = "mm"
    private val cm = "cm"
    private val mPerS = "m/s"
    private val percent = "%"
    private val hour24 = "24:00"
    private val lessThan1mm = "~1mm"
    private val lessThan1cm = "~1cm"
    private val rainDrop = "빗방울"
    private val snowBlizzard = "눈날림"

    private val conditionDescriptionsMap = mapOf(
        "비" to "흐리고 비",
        "비/눈" to "흐리고 비/눈",
        "눈" to "흐리고 눈",
        "빗방울" to "흐리고 비",
        "빗방울/눈날림" to "흐리고 비/눈",
        "눈날림" to "흐리고 눈",
        "구름 많음" to "구름많음",
    )

    private val windDirectionMap = mapOf(
        "북북동" to 25,
        "북동" to 45,
        "동북동" to 67,
        "동" to 90,
        "동남동" to 112,
        "남동" to 135,
        "남남동" to 157,
        "남" to 180,
        "남남서" to 202,
        "남서" to 225,
        "서남서" to 247,
        "서" to 270,
        "서북서" to 292,
        "북서" to 315,
        "북북서" to 337,
        "북" to 0,
    )

    fun parseCurrentConditions(document: Document, baseDateTime: String): ParsedKmaCurrentWeather {
        //기온(5.8) tmp, 체감(체감 5.8℃) chill, 어제와 기온이 같아요 w-txt, 습도(40) lbl ic-hm val
        //바람(북서 1.1) lbl ic-wind val, 1시간 강수량(-) lbl rn-hr1 ic-rn val
        //발효중인 특보 cmp-impact-fct
        val rootElements = document.getElementsByClass("cmp-cur-weather")
        val wrap1 = rootElements.select("ul.wrap-1")
        val wrap2 = rootElements.select("ul.wrap-2.no-underline")
        val wIconwTemp = wrap1.select(".w-icon.w-temp")
        val li = wIconwTemp[0]
        val spans = li.getElementsByTag("span")
        val pty = spans[1].text()
        //4.8℃ 최저-최고-
        val temp = spans[3].textNodes()[0].text().replace(" ", "")

        //1일전 기온
        var yesterdayTemp = wrap1.select("li.w-txt").text().replace(" ", "")
        if (yesterdayTemp.contains("℃")) {
            val t = yesterdayTemp.replace("어제보다", "").replace("높아요", "").replace("낮아요", "").replace("℃", "")
            val currentTempVal = temp.toDouble()
            var yesterdayTempVal = t.toDouble()
            if (yesterdayTemp.contains("높아요")) {
                yesterdayTempVal = currentTempVal - yesterdayTempVal
            } else if (yesterdayTemp.contains("낮아요")) {
                yesterdayTempVal += currentTempVal
            }
            yesterdayTemp = yesterdayTempVal.toString()
        } else {
            yesterdayTemp = temp
        }

        //체감(4.8℃)
        var chill = spans.select(".chill").text()
        chill = chill.substring(3, chill.length - 2).replace(" ", "")

        // 43 % 동 1.1 m/s - mm
        val spans2 = wrap2.select("span.val")
        val humidity = spans2[0].text().replace(" ", "")
        var windDirection = ""
        var windSpeed = ""
        val wind = spans2[1].text()
        if (wind != "-") {
            val spWind = wind.split(" ").toTypedArray()
            windDirection = spWind[0].replace(" ", "")
            windSpeed = spWind[1].replace(" ", "")
        }
        var precipitationVolume = spans2[2].text().replace(" ", "")
        if (precipitationVolume.contains("-")) {
            precipitationVolume = "mm"
        }

        if (precipitationVolume.contains("~")) {
            precipitationVolume = PrecipitationValueType.rainDrop.value.toString()
        }

        return ParsedKmaCurrentWeather(
            temperature = temp.toTemperature().toInt().toShort(), feelsLikeTemperature = chill.toTemperature().toInt().toShort(), humidity =
            humidity
                .toHumidity(),
            precipitationType = pty,
            windDirection = windDirection.toWindDirection(), windSpeed = windSpeed.toWindSpeed(),
            precipitationVolume = precipitationVolume.toPrecipitationVolume(),
            dateTime = baseDateTime, yesterdayTemperature = yesterdayTemp.toTemperature().toInt().toShort(),
        )
    }

    fun parseHourlyForecasts(document: Document): List<ParsedKmaHourlyForecast> {
        val elements = document.getElementsByClass("slide-wrap")

        //오늘, 내일, 모레, 글피, 그글피
        val slides = elements.select("div.slide")
        val parsedKmaHourlyForecasts = mutableListOf<ParsedKmaHourlyForecast>()
        var zonedDateTime = ZonedDateTime.now(zoneId)
        var localDate: LocalDate?
        var localTime: LocalTime?
        var date: String?
        var time: String?
        var weatherCondition: String
        var temp: String
        var feelsLikeTemp: String
        var pop: String
        var windDirection: String
        var windSpeed: String
        var humidity: String
        var thunder: Boolean
        var hasShower = false

        for (slide in slides) {
            val uls = slide.getElementsByClass("item-wrap").select("ul")
            if (slide.hasClass("slide day-ten")) {
                break
            }
            for (ul in uls) {

                val lis = ul.getElementsByTag("li")
                date = ul.attr("data-date")
                localDate = LocalDate.parse(date)
                time = ul.attr("data-time")
                if (time == hour24) {
                    time = "00:00"
                    localDate = localDate.plusDays(1)
                }
                localTime = LocalTime.parse(time)
                localTime = localTime.withMinute(0).withSecond(0).withNano(0)
                zonedDateTime = ZonedDateTime.of(localDate, localTime, zonedDateTime.zone)
                if (ul.hasAttr("data-sonagi")) {
                    hasShower = ul.attr("data-sonagi") == "1"
                }
                weatherCondition = lis[1].getElementsByTag("span")[1].text()
                thunder = if (lis[1].getElementsByTag("span").size >= 3) {
                    lis[1].getElementsByTag("span")[2].className() == "lgt"
                } else {
                    false
                }
                temp = lis[2].getElementsByTag("span")[1].childNode(0).toString().replace(degree, "")
                feelsLikeTemp = lis[3].getElementsByTag("span")[1].text().replace(degree, "")/*
                강우+강설
                <li class="pcp snow-exists">
                <span class="hid">강수량: </span>
                <span>~1<span class="unit">mm</span><br/>~1<span class="unit">cm</span></span>  ~1mm~1cm
                </li>

                강수(현재 시간대에는 강설이 없으나, 다른 시간대에 강설이 있는 경우)
                <li class="pcp snow-exists">
                <span class="hid">강수량: </span>
                <span>~1<span class="unit">mm</span><br/>-</span>   ~1mm-
                </li>

                강수
                <li class="pcp ">
                <span class="hid">강수량: </span>
                <span>~1<span class="unit">mm</span></span>     ~1mm
                </li>

                눈날림
                <li class="pcp vs-txt-rn">
                <span class="hid">강수량: </span>
                <span>눈날림<span class="unit">mm</span></span>     눈날림mm
                </li>

                빗방울+눈날림
                <li class="pcp vs-txt-rn">
                <span class="hid">강수량: </span>
                <span>빗방울<br>눈날림<span class="unit">mm</span></span>    빗방울눈날림mm
                </li>

                강수없음
                <li class="pcp snow-exists">
                <span class="hid">강수량: </span>
                <span>-<br/>-</span>
                </li>

                <li class="pcp ">
                <span class="hid">강수량: </span>
                <span>-</span>
                </li>

                <li class="pcp">
                <span class="hid">강수량: </span>
                <span>1시간 단위 강수량(적설포함)은 모레까지 제공합니다.</span>
                </li>


                ~1mm~1cm
                5mm~1cm
                15mm15cm
                ~1mm-
                -~1cm
                ~1mm
                ~1cm
                10mm
                10cm
                눈날림mm
                빗방울눈날림mm
                 */
                val pcpText = lis[4].getElementsByTag("span")[1].text()
                var index: Int

                var hasRain = false
                var rainVolume = ""
                var hasSnow = false
                var snowVolume = ""

                if (pcpText.contains(mm) || pcpText.contains(cm)) {
                    if (pcpText.contains(rainDrop)) {
                        hasRain = true
                        rainVolume = rainDrop
                    }
                    if (pcpText.contains(snowBlizzard)) {
                        hasSnow = true
                        snowVolume = snowBlizzard
                    }
                    if (pcpText.contains(lessThan1mm)) {
                        hasRain = true
                        rainVolume = lessThan1mm
                    } else if (pcpText.contains(mm) && !hasRain) {
                        index = pcpText.indexOf(mm)
                        val subStr = pcpText.substring(0, index)
                        if (!subStr.contains(rainDrop) && !subStr.contains(snowBlizzard)) {
                            hasRain = true
                            rainVolume = subStr + mm
                        }
                    }
                    if (pcpText.contains(lessThan1cm)) {
                        hasSnow = true
                        snowVolume = lessThan1cm
                    } else if (pcpText.contains(cm) && !hasSnow) {
                        index = pcpText.indexOf(cm)
                        var firstIndex = 0
                        if (pcpText.contains(mm)) {
                            firstIndex = pcpText.indexOf(mm) + 2
                        }
                        val subStr = pcpText.substring(firstIndex, index)
                        if (!subStr.contains(rainDrop) && !subStr.contains(snowBlizzard)) {
                            hasSnow = true
                            snowVolume = subStr + cm
                        }
                    }
                }
                pop = lis[5].getElementsByTag("span")[1].text()
                windDirection = lis[6].getElementsByTag("span")[1].text()
                if (lis[6].getElementsByTag("span").size >= 3) {
                    windSpeed = lis[6].getElementsByTag("span")[2].text()
                } else {
                    windDirection = ""
                    windSpeed = ""
                }
                humidity = lis[7].getElementsByTag("span")[1].text()

                if (rainVolume.contains("~")) {
                    rainVolume = PrecipitationValueType.rainDrop.value.toString()
                }
                if (snowVolume.contains("~")) {
                    snowVolume = PrecipitationValueType.snowDrop.value.toString()
                }

                parsedKmaHourlyForecasts.add(
                    ParsedKmaHourlyForecast(
                        dateTime = zonedDateTime.toString(),
                        weatherCondition = weatherCondition,
                        temp = temp.toTemperature().toInt().toShort(),
                        feelsLikeTemp = feelsLikeTemp.toTemperature().toInt().toShort(),
                        pop = pop.toPop(),
                        windDirection = windDirection.toWindDirection(),
                        windSpeed = windSpeed.toWindSpeed(),
                        humidity = humidity.toHumidity(),
                        isHasShower = hasShower,
                        isHasThunder = thunder,
                        isHasRain = hasRain,
                        rainVolume = rainVolume.toPrecipitationVolume(),
                        isHasSnow = hasSnow,
                        snowVolume = snowVolume.toPrecipitationVolume(),
                    ),
                )
            }
        }
        return parsedKmaHourlyForecasts
    }

    fun parseDailyForecasts(document: Document): MutableList<ParsedKmaDailyForecast> {
        val elements = document.getElementsByClass("slide-wrap")
        //이후 10일
        val slides = elements.select("div.slide.day-ten div.daily")
        var zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        zonedDateTime = zonedDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0)
        var localDate: LocalDate?
        var date: String?
        var weatherDescription: String?
        var minTemp: String
        var maxTemp: String
        var pop: Int
        val parsedKmaDailyForecasts: MutableList<ParsedKmaDailyForecast> = mutableListOf()

        for (daily in slides) {
            val uls = daily.getElementsByClass("item-wrap").select("ul")
            date = daily.attr("data-date")
            localDate = LocalDate.parse(date)
            zonedDateTime = zonedDateTime.withYear(localDate.year).withMonth(localDate.monthValue).withDayOfMonth(localDate.dayOfMonth)

            var am: ParsedKmaDailyForecast.Values? = null
            var pm: ParsedKmaDailyForecast.Values? = null
            var single: ParsedKmaDailyForecast.Values? = null

            if (uls.size == 2) {
                //am, pm
                val amLis = uls[0].getElementsByTag("li")
                val pmLis = uls[1].getElementsByTag("li")
                weatherDescription = amLis[1].getElementsByTag("span")[1].text()
                minTemp = amLis[2].getElementsByTag("span")[1].text()
                minTemp = minTemp.substring(3, minTemp.length - 1)
                pop = amLis[3].getElementsByTag("span")[1].text().toPop()

                am = ParsedKmaDailyForecast.Values(
                    weatherDescription = weatherDescription,
                    pop = pop,
                )

                weatherDescription = pmLis[1].getElementsByTag("span")[1].text()
                maxTemp = pmLis[2].getElementsByTag("span")[1].text()
                maxTemp = maxTemp.substring(3, maxTemp.length - 1)
                pop = pmLis[3].getElementsByTag("span")[1].text().toPop()

                pm = ParsedKmaDailyForecast.Values(
                    weatherDescription = weatherDescription,
                    pop = pop,
                )
            } else {
                //single
                val lis = uls[0].getElementsByTag("li")
                weatherDescription = lis[1].getElementsByTag("span")[1].text()
                val temps = lis[2].getElementsByTag("span")[1].text().split(" / ").toTypedArray()
                minTemp = temps[0].substring(3, temps[0].length - 1)
                maxTemp = temps[1].substring(3, temps[1].length - 1)
                pop = lis[3].getElementsByTag("span")[1].text().toPop()

                single = ParsedKmaDailyForecast.Values(
                    weatherDescription = weatherDescription,
                    pop = pop,
                )
            }
            parsedKmaDailyForecasts.add(
                ParsedKmaDailyForecast(
                    minTemp = minTemp.toTemperature().toInt().toShort(), maxTemp = maxTemp.toTemperature().toInt().toShort(), date = zonedDateTime
                        .toString(),
                    isSingle = single == null, amValues = am, pmValues = pm,
                    singleValues = single,
                ),
            )
        }
        return parsedKmaDailyForecasts
    }

    fun makeExtendedDailyForecasts(
        hourlyForecasts: List<ParsedKmaHourlyForecast>,
        dailyForecasts: MutableList<ParsedKmaDailyForecast>,
    ): List<ParsedKmaDailyForecast> {
        val firstDateTimeOfDaily = ZonedDateTime.parse(dailyForecasts[0].date)
        val krZoneId = firstDateTimeOfDaily.zone

        val criteriaDateTime = ZonedDateTime.now(krZoneId).withHour(23).withMinute(59)
        var beginIdx = hourlyForecasts.indexOfFirst { criteriaDateTime.isBefore(ZonedDateTime.parse(it.dateTime)) }

        var minTemp = Short.MAX_VALUE
        var maxTemp = Short.MIN_VALUE
        var hours: Int
        var amSky = ""
        var pmSky = ""
        var amPop = 0
        var pmPop = 0
        var dateTime: ZonedDateTime?
        var temp: Short
        var hourlyForecastItemDateTime: ZonedDateTime?

        while (beginIdx < hourlyForecasts.size) {
            hourlyForecastItemDateTime = ZonedDateTime.parse(hourlyForecasts[beginIdx].dateTime)

            if (firstDateTimeOfDaily.dayOfYear == hourlyForecastItemDateTime.dayOfYear) {
                if (hourlyForecastItemDateTime.hour == 1) {
                    break
                }
            }

            hours = hourlyForecastItemDateTime.hour
            if (hours == 0 && minTemp != Short.MAX_VALUE) {
                dateTime = ZonedDateTime.of(
                    hourlyForecastItemDateTime.toLocalDateTime(),
                    hourlyForecastItemDateTime.zone,
                )
                dateTime = dateTime.minusDays(1)
                dailyForecasts.add(
                    ParsedKmaDailyForecast(
                        date = dateTime.toString(),
                        amValues = ParsedKmaDailyForecast.Values(
                            pop = amPop,
                            weatherDescription = amSky,
                        ),
                        pmValues = ParsedKmaDailyForecast.Values(
                            pop = pmPop,
                            weatherDescription = pmSky,
                        ),
                        minTemp = minTemp, maxTemp = maxTemp,
                    ),
                )
                minTemp = Short.MAX_VALUE
                maxTemp = Short.MIN_VALUE
            } else {
                temp = hourlyForecasts[beginIdx].temp
                minTemp = minOf(minTemp, temp)
                maxTemp = maxOf(maxTemp, temp)

                if (hours == 9) {
                    amSky = convertHourlyWeatherDescriptionToMid(hourlyForecasts[beginIdx].weatherCondition)
                    amPop = hourlyForecasts[beginIdx].pop
                } else if (hours == 15) {
                    pmSky = convertHourlyWeatherDescriptionToMid(hourlyForecasts[beginIdx].weatherCondition)
                    pmPop = hourlyForecasts[beginIdx].pop
                }
            }
            beginIdx++
        }

        dailyForecasts.sortWith { t1, t2 -> t1.date.compareTo(t2.date) }
        return dailyForecasts
    }

    private fun convertHourlyWeatherDescriptionToMid(description: String): String {/*
    hourly -
        <item>맑음</item>
        <item>구름 많음</item>
        <item>흐림</item>
        <item>비</item>
        <item>비/눈</item>
        <item>눈</item>
        <item>소나기</item>
        <item>빗방울</item>
        <item>빗방울/눈날림</item>
        <item>눈날림</item>

    mid -
        <item>맑음</item>
        <item>구름많음</item>
        <item>구름많고 비</item>
        <item>구름많고 눈</item>
        <item>구름많고 비/눈</item>
        <item>구름많고 소나기</item>
        <item>흐림</item>
        <item>흐리고 비</item>
        <item>흐리고 눈</item>
        <item>흐리고 비/눈</item>
        <item>흐리고 소나기</item>
        <item>소나기</item>
     */
        return conditionDescriptionsMap[description] ?: description
    }


    private fun String.toWindDirection(): Int = windDirectionMap[this] ?: 0

    private fun String.toTemperature(): Double = toDoubleOrNull() ?: 0.0

    private fun String.toHumidity(): Int = replace(percent, "").toIntOrNull() ?: 0

    private fun String.toWindSpeed(): Double = replace(mPerS, "").toDoubleOrNull() ?: 0.0

    private fun String.toPrecipitationVolume(): Double =
        replace("0.0", "").replace(mm, "").replace(cm, "").toDoubleOrNull() ?: PrecipitationValueType.none.value

    private fun String.toPop(): Int = replace(percent, "").toIntOrNull() ?: ProbabilityValueType.none.value
}