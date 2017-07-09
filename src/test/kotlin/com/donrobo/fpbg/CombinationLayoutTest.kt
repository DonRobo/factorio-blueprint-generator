package com.donrobo.fpbg

import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.generator.layout.buildCombinationLayout
import org.junit.Assert.assertEquals
import org.junit.Test

class CombinationLayoutTest {

    @Test
    fun testCombinationLayout() {
        for (i in 1..20) {
            val layout = buildCombinationLayout("test", 0, 0, Direction.UP, i)
            layout.generateBlueprint()
            assertEquals(1, layout.inputs.size)
            assertEquals(i, layout.outputs.size)
        }

    }
}