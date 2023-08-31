package io.github.pknujsp.weatherwizard.core.network.datasource.kma.parser


import org.jsoup.nodes.Document
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
 class KmaHtmlParser @Inject constructor() {

    private val conditionDescriptionsMap = mapOf(
        "비" to "흐리고 비",
        "비/눈" to "흐리고 비/눈",
        "눈" to "흐리고 눈",
        "빗방울" to "흐리고 비",
        "빗방울/눈날림" to "흐리고 비/눈",
        "눈날림" to "흐리고 눈",
        "구름 많음" to "구름많음",
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
            val t = yesterdayTemp.replace("어제보다", "").replace("높아요", "")
                .replace("낮아요", "").replace("℃", "")
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
            precipitationVolume = "0.0mm"
        }

        return ParsedKmaCurrentWeather(
            temp = temp, feelsLikeTemp = chill, humidity = humidity, pty = pty,
            windDirection = windDirection, windSpeed = windSpeed, precipitationVolume = precipitationVolume,
            baseDateTimeISO8601 = baseDateTime, yesterdayTemp = yesterdayTemp,
        )
    }

    fun parseHourlyForecasts(document: Document): List<ParsedKmaHourlyForecast> {
        val elements = document.getElementsByClass("slide-wrap")

        //오늘, 내일, 모레, 글피, 그글피
        val slides = elements.select("div.slide")
        val parsedKmaHourlyForecasts = mutableListOf<ParsedKmaHourlyForecast>()
        var zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        var localDate: LocalDate? = null
        var localTime: LocalTime? = null
        var date: String? = null
        var time: String? = null
        var weatherDescription: String = ""
        var temp: String = ""
        var feelsLikeTemp: String = ""
        var pop: String = ""
        var windDirection: String = ""
        var windSpeed: String = ""
        var humidity: String = ""
        var thunder = false
        var hasShower = false
        val hour24 = "24:00"
        val degree = "℃"
        val mm = "mm"
        val cm = "cm"
        val lessThan1mm = "~1mm"
        val lessThan1cm = "~1cm"
        val rainDrop = "빗방울"
        val snowBlizzard = "눈날림"

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
                weatherDescription = lis[1].getElementsByTag("span")[1].text()
                thunder = if (lis[1].getElementsByTag("span").size >= 3) {
                    lis[1].getElementsByTag("span")[2].className() == "lgt"
                } else {
                    false
                }
                temp = lis[2].getElementsByTag("span")[1].childNode(0).toString().replace(degree, "")
                feelsLikeTemp = lis[3].getElementsByTag("span")[1].text().replace(degree, "")
                /*
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
                var index = 0

                var hasRain = false
                var rainVolume: String = ""
                var hasSnow = false
                var snowVolume: String = ""

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

                parsedKmaHourlyForecasts.add(
                    ParsedKmaHourlyForecast(
                        hourISO8601 = zonedDateTime.toString(), weatherDescription = weatherDescription,
                        temp = temp, feelsLikeTemp = feelsLikeTemp, pop = pop,
                        windDirection = windDirection, windSpeed = windSpeed, humidity = humidity,
                        isHasShower = hasShower, isHasThunder = thunder,
                        isHasRain = hasRain, rainVolume = rainVolume,
                        isHasSnow = hasSnow, snowVolume = snowVolume,
                    ),
                )
            }
        }
        return parsedKmaHourlyForecasts
    }

    fun parseDailyForecasts(document: Document): List<ParsedKmaDailyForecast> {
        val elements = document.getElementsByClass("slide-wrap")
        //이후 10일
        val slides = elements.select("div.slide.day-ten div.daily")
        var zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        zonedDateTime = zonedDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0)
        var localDate: LocalDate? = null
        var date: String? = null
        var weatherDescription: String? = null
        var minTemp: String? = null
        var maxTemp: String? = null
        var pop: String? = null
        val parsedKmaDailyForecasts: MutableList<ParsedKmaDailyForecast> = arrayListOf()

        for (daily in slides) {
            val uls = daily.getElementsByClass("item-wrap").select("ul")
            date = daily.attr("data-date")
            localDate = LocalDate.parse(date)
            zonedDateTime = zonedDateTime.withYear(localDate.year).withMonth(localDate.monthValue)
                .withDayOfMonth(localDate.dayOfMonth)

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
                pop = amLis[3].getElementsByTag("span")[1].text()

                am = ParsedKmaDailyForecast.Values(
                    weatherDescription = weatherDescription,
                    pop = pop,
                )

                weatherDescription = pmLis[1].getElementsByTag("span")[1].text()
                maxTemp = pmLis[2].getElementsByTag("span")[1].text()
                maxTemp = maxTemp.substring(3, maxTemp.length - 1)
                pop = pmLis[3].getElementsByTag("span")[1].text()

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
                pop = lis[3].getElementsByTag("span")[1].text()

                single = ParsedKmaDailyForecast.Values(
                    weatherDescription = weatherDescription,
                    pop = pop,
                )
            }
            parsedKmaDailyForecasts.add(
                ParsedKmaDailyForecast(
                    minTemp = minTemp, maxTemp = maxTemp, dateISO8601 = zonedDateTime.toString(),
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
        val firstDateTimeOfDaily = ZonedDateTime.parse(dailyForecasts[0].dateISO8601)
        val krZoneId = firstDateTimeOfDaily.zone

        var criteriaDateTime = ZonedDateTime.now(krZoneId)
        criteriaDateTime = criteriaDateTime.withHour(23)
        criteriaDateTime = criteriaDateTime.withMinute(59)
        var beginIdx = 0

        while (beginIdx < hourlyForecasts.size) {
            if (criteriaDateTime.isBefore(ZonedDateTime.parse(hourlyForecasts[beginIdx].hourISO8601)))
                break
            beginIdx++
        }
        var minTemp = Int.MAX_VALUE
        var maxTemp = Int.MIN_VALUE
        var hours = 0
        var amSky: String = ""
        var pmSky: String = ""
        var amPop: String = ""
        var pmPop: String = ""
        var dateTime: ZonedDateTime? = null
        var temp = 0
        var hourlyForecastItemDateTime: ZonedDateTime? = null

        while (beginIdx < hourlyForecasts.size) {
            hourlyForecastItemDateTime = ZonedDateTime.parse(hourlyForecasts[beginIdx].hourISO8601)

            if (firstDateTimeOfDaily.dayOfYear == hourlyForecastItemDateTime.dayOfYear) {
                if (hourlyForecastItemDateTime.hour == 1) {
                    break
                }
            }
            hours = hourlyForecastItemDateTime.hour
            if (hours == 0 && minTemp != Int.MAX_VALUE) {
                dateTime = ZonedDateTime.of(
                    hourlyForecastItemDateTime.toLocalDateTime(),
                    hourlyForecastItemDateTime.zone,
                )
                dateTime = dateTime.minusDays(1)
                dailyForecasts.add(
                    ParsedKmaDailyForecast(
                        dateISO8601 = dateTime.toString(),
                        amValues = ParsedKmaDailyForecast.Values(
                            pop = amPop,
                            weatherDescription = amSky,
                        ),
                        pmValues = ParsedKmaDailyForecast.Values(
                            pop = pmPop,
                            weatherDescription = pmSky,
                        ),
                        minTemp = minTemp.toString(), maxTemp = maxTemp.toString(),
                    ),
                )
                minTemp = Int.MAX_VALUE
                maxTemp = Int.MIN_VALUE
            } else {
                temp = hourlyForecasts[beginIdx].temp.toInt()
                minTemp = minOf(minTemp, temp)
                maxTemp = maxOf(maxTemp, temp)

                if (hours == 9) {
                    amSky = convertHourlyWeatherDescriptionToMid(hourlyForecasts[beginIdx].weatherDescription)
                    amPop = hourlyForecasts[beginIdx].pop
                } else if (hours == 15) {
                    pmSky = convertHourlyWeatherDescriptionToMid(hourlyForecasts[beginIdx].weatherDescription)
                    pmPop = hourlyForecasts[beginIdx].pop
                }
            }
            beginIdx++
        }

        dailyForecasts.sortWith { t1, t2 -> t1.dateISO8601.compareTo(t2.dateISO8601) }
        return dailyForecasts
    }

    private fun convertHourlyWeatherDescriptionToMid(description: String): String {
        /*
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
        return if (conditionDescriptionsMap.containsKey(description)) {
            conditionDescriptionsMap[description] ?: ""
        } else {
            description
        }
    }

}