package com.donrobo.fpbg.generator.layout

import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.DOWN
import com.donrobo.fpbg.blueprint.Direction.UP
import com.donrobo.fpbg.data.Int2
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

                if (dir == UP && i == 3) {
                    assertEquals(Int2(0, 0), layout.inputs.single().position)

                    assertEquals(Int2(1, 0), layout.outputs[0].position)
                    assertEquals(Int2(-1, -1), layout.outputs[1].position)
                    assertEquals(Int2(0, -1), layout.outputs[2].position)
                }
                if (dir == DOWN && i == 3) {
                    assertEquals(Int2(0, 0), layout.inputs.single().position)

                    assertEquals(Int2(-1, 0), layout.outputs[0].position)
                    assertEquals(Int2(1, 1), layout.outputs[1].position)
                    assertEquals(Int2(0, 1), layout.outputs[2].position)
                }
            }
        }
    }
}