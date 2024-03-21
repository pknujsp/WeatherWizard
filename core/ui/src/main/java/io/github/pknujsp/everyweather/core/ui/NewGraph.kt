package io.github.pknujsp.everyweather.core.ui

class NewGraph(
    private val values: List<List<Int>>,
) {
    private val minValue: Int = values.minOf { v -> v.minOf { it } }
    private val maxValue: Int = values.maxOf { v -> v.maxOf { it } }

    fun createNewGraph(height: Float): List<List<LinePoint>> {
        val valueLength = maxValue - minValue

        val points =
            values.map { vals ->
                val centerPoints =
                    vals.mapIndexed { i, value ->
                        calculateYPosition(value, minValue, valueLength) * height
                    }

                var left: Float
                var right = 0f
                val lastIndex = vals.lastIndex

                centerPoints.mapIndexed { i, center ->
                    left =
                        if (i > 0) {
                            right
                        } else {
                            -1f
                        }

                    right =
                        if (i < lastIndex) {
                            center + if (center < centerPoints[i + 1]) (centerPoints[i + 1] - center) / 2 else (center - centerPoints[i + 1]) / -2
                        } else {
                            -1f
                        }

                    LinePoint(left, center, right, height)
                }
            }

        return points
    }

    private fun calculateYPosition(
        value: Int,
        minValue: Int,
        valueLength: Int,
    ) = 1f - ((value - minValue).toFloat() / valueLength)

    data class LinePoint(
        val leftY: Float,
        val centerY: Float,
        val rightY: Float,
        val drawingBoxHeight: Float,
    )
}
