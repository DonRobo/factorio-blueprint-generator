package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Direction
import org.junit.Assert.assertEquals
import org.junit.Test

class CombinationLayoutTest {

    @Test
    fun testCombinationLayout() {
        Direction.values().forEach { dir ->
            for (i in 1..20) {
                println("==$i $dir==")
                val layout = buildCombinationLayout("test", 0, 0, dir, i)
                println(layout.generateBlueprint().visualize())
                assertEquals(1, layout.inputs.size)
                assertEquals(i, layout.outputs.size)
            }
        }
    }
}