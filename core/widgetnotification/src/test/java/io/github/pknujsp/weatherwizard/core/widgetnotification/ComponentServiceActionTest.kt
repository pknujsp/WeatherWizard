package io.github.pknujsp.weatherwizard.core.widgetnotification

import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ComponentServiceActionTest {

    @Test
    fun bundle_to_action() {
        val action = ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument("test", intArrayOf(10, 20)))
        val map = action.argument.toBundle()
        val action2 = ComponentServiceAction.toInstance(map)
        assertEquals(action.argument, action2.argument)
    }

    @Test
    fun map_to_action() {
        val action = ComponentServiceAction.LoadWidgetData(LoadWidgetDataArgument("test", intArrayOf(10, 20)))
        val map = action.argument.toMap()
        val action2 = ComponentServiceAction.toInstance(map)
        assertEquals(action.argument, action2.argument)
    }
}