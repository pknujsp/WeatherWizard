package io.github.pknujsp.common

import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.time.ZonedDateTime
import java.util.TimeZone

@RunWith(JUnit4::class)
class DayNightCalcTest {
    // DayNightCalculatorTest

    @Test
    fun test() {
        val latitude = 37.5665
        val longitude = 126.9780

        val calculator = DayNightCalculator(latitude, longitude)

        // 새벽
        val firstCase = ZonedDateTime.of(2023, 9, 16, 2, 0, 0, 0, TimeZone.getDefault().toZoneId())
        // 낮
        val secondCase = ZonedDateTime.of(2023, 9, 16, 12, 0, 0, 0, TimeZone.getDefault().toZoneId())
        // 저녁
        val thirdCase = ZonedDateTime.of(2023, 9, 16, 23, 0, 0, 0, TimeZone.getDefault().toZoneId())

        val firstCaseResult = calculator.getSunSetRiseTimes(firstCase.toCalendar())
        val secondCaseResult = calculator.getSunSetRiseTimes(secondCase.toCalendar())
        val thirdCaseResult = calculator.getSunSetRiseTimes(thirdCase.toCalendar())

        println("firstCaseResult: $firstCaseResult")
        println("secondCaseResult: $secondCaseResult")
        println("thirdCaseResult: $thirdCaseResult")
    }
}
