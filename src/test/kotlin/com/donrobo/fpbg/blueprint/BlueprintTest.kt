package com.donrobo.fpbg.blueprint

import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.building.Splitter
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.data.Int2
import org.junit.Assert.*
import org.junit.Test

class BlueprintTest {

    /**
     * ^ ^
     *
     * should become
     *
     * >
     *
     * >
     *
     * and
     *
     * v v
     *
     * and
     *
     * <
     *
     * <
     */
    @Test
    fun simpleRotation() {
        val blueprint = Blueprint()

        blueprint.addBuilding(YellowBelt(0, 0, UP))
        blueprint.addBuilding(YellowBelt(2, 0, UP))

        assertEquals("^ ^", blueprint.visualize())

        assertEquals(">\n \n>", blueprint.rotateCCW(Int2(0, 0), 1).visualize())
        assertEquals("v v", blueprint.rotateCCW(Int2(0, 0), 2).visualize())
        assertEquals("<\n \n<", blueprint.rotateCCW(Int2(0, 0), 3).visualize())
    }

    @Test
    fun offsetRotation() {
        val blueprint = Blueprint()

        blueprint.addBuilding(YellowBelt(0, 0, UP))
        blueprint.addBuilding(YellowBelt(2, 0, UP))

        assertFalse(blueprint.isOccupied(0, -1))
        assertEquals("^ ^", blueprint.visualize())

        assertEquals(">\n \n>", blueprint.rotateCCW(Int2(1, 0), 1).visualize())
        assertTrue(blueprint.rotateCCW(Int2(1, 0), 1).isOccupied(1, -1))
        assertEquals("v v", blueprint.rotateCCW(Int2(1, 0), 2).visualize())
        assertEquals("<\n \n<", blueprint.rotateCCW(Int2(1, 0), 3).visualize())
    }

    @Test
    fun splitterRotation() {
        val blueprint = Blueprint()

        blueprint.addBuilding(YellowBelt(0, 0, RIGHT))
        blueprint.addBuilding(YellowBelt(0, 1, RIGHT))
        blueprint.addBuilding(Splitter(1, 0, RIGHT))
        blueprint.addBuilding(YellowBelt(2, 0, UP))
        blueprint.addBuilding(YellowBelt(2, 1, DOWN))

        val splitterFromBlueprint = blueprint[1, 1]
        assertTrue(splitterFromBlueprint is Splitter)
        assertTrue((splitterFromBlueprint as Splitter).direction == RIGHT)
        assertEquals(">S^\n>Sv", blueprint.visualize())

        val rotate1 = blueprint.rotateCCW(Int2(0, 0), 1)
        val rotate3 = blueprint.rotateCCW(Int2(0, 0), 3)
        val rotate2 = blueprint.rotateCCW(Int2(0, 0), 2)

        assertEquals("vv\nSS\n<>", rotate1.visualize())
        assertTrue((rotate1[0, 1] as Splitter).direction == DOWN)
        assertTrue((rotate1[-1, 1] as Splitter).direction == DOWN)

        assertEquals("^S<\nvS<", rotate2.visualize())
        assertTrue((rotate2[-1, 0] as Splitter).direction == LEFT)
        assertTrue((rotate2[-1, -1] as Splitter).direction == LEFT)

        assertEquals("<>\nSS\n^^", rotate3.visualize())
        assertTrue((rotate3[0, -1] as Splitter).direction == UP)
        assertTrue((rotate3[1, -1] as Splitter).direction == UP)
    }
}
